package repository

import (
	"context"
	"log/slog"
	"notime/bootstrap"
	"notime/domain"
	"notime/external/recsys_engine"
	"notime/external/sql/store"
	"notime/utils/htmlutils"
	"sync"
	"time"
)

type RecsysEngine interface {
	GetRelatedArticleUrlsFromUrl(url string, timeout time.Duration) ([]string, error)
}

func NewRecsysEngine(host string, port string) RecsysEngine {
	return &recsys_engine.RecsysEngineImpl{Host: host, Port: port}
}

type RecsysRepository interface {
	GetLatestArticlesFromSubscribed(ctx context.Context, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error)
	GetLatestArticlesFromUnsubscribed(ctx context.Context, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error)
	GetLatestArticlesByPublisher(ctx context.Context, publisherId int32, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error)
	GetRelatedArticles(ctx context.Context, articleId int64, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error)
}

type RecsysRepositoryImpl struct {
	q      *store.Queries
	env    *bootstrap.Env
	engine RecsysEngine
	UtilitiesRepository
}

func NewRecsysRepository(env *bootstrap.Env, q *store.Queries) RecsysRepository {
	engine := NewRecsysEngine(env.RecsysHost, env.RecsysPort)
	return &RecsysRepositoryImpl{
		q:                   q,
		env:                 env,
		engine:              engine,
		UtilitiesRepository: UtilitiesRepository{q: q},
	}
}

func (r *RecsysRepositoryImpl) GetLatestArticlesFromSubscribed(ctx context.Context, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error) {
	dbArticles, err := r.q.GetArticlesFromSubscribedPublishers(ctx, store.GetArticlesFromSubscribedPublishersParams{
		UserID: userId,
		Count:  int32(count),
		Offset: int32(offset),
	})
	if err != nil {
		slog.Error("[Recsys Repository] GetLatestArticlesFromSubscribed query:", "error", err)
		return nil, err
	}

	var wg sync.WaitGroup
	dmArticles := make([]*domain.ArticleMetadata, 0)
	errCh := make(chan error, len(dbArticles))
	resCh := make(chan *domain.ArticleMetadata, len(dbArticles))

	for _, dbArticle := range dbArticles {
		wg.Add(1)
		go func(dbArticle store.Post) {
			defer wg.Done()

			// Check if url is actually of an article
			isArticle, err := htmlutils.ValidateUrlAsArticle(dbArticle.Url, r.env.PElementCharCount, r.env.PElementThreshold)
			if err != nil {
				slog.Error("[Article Repository] GetByPublisher validate url as article:", "error", err)
				errCh <- err
				return
			}
			if !isArticle {
				slog.Warn("[Article Repository] GetByPublisher: Skipping potential article: url is not an article:", "url", dbArticle.Url)
				return
			}

			dmArticle, err := r.completeDmArticleFromDb(ctx, &dbArticle)
			if err != nil {
				slog.Error("[Article Repository] GetByPublisher convert:", "error", err)
				errCh <- err
				return
			}
			resCh <- dmArticle
		}(dbArticle)
	}

	// Wait for all goroutines to finish
	wg.Wait()

	// Close the channels after all goroutines finish
	close(errCh)
	close(resCh)

	// Check if there were any errors
	for err := range errCh {
		if err != nil {
			return nil, err
		}
	}

	// Collect all results
	for dmArticle := range resCh {
		dmArticles = append(dmArticles, dmArticle)
	}

	return dmArticles, nil
}

func (r *RecsysRepositoryImpl) GetLatestArticlesFromUnsubscribed(ctx context.Context, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error) {
	dbSubscribedPublishers, err := r.q.GetSubscribedPublishersByUserId(ctx, userId)
	if err != nil {
		slog.Error("[Recsys Repository] GetLatestArticlesFromUnsubscribed query:", "error", err)
		return nil, err
	}

	if len(dbSubscribedPublishers) > 0 {
		var dmArticles []*domain.ArticleMetadata
		for _, dbPublisher := range dbSubscribedPublishers {
			articleUrls, err := r.engine.GetRelatedArticleUrlsFromUrl(dbPublisher.Url, time.Duration(r.env.ContextTimeout)*time.Second)
			if err != nil {
				slog.Error("[Recsys Repository] GetLatestArticlesFromUnsubscribed engine:", "error", err)
				continue
			}

			var wg sync.WaitGroup
			errCh := make(chan error, len(articleUrls))
			resCh := make(chan *domain.ArticleMetadata, len(articleUrls))

			for _, articleUrl := range articleUrls {
				wg.Add(1)
				go func(articleUrl string) {
					defer wg.Done()

					// Check if url is actually of an article
					isArticle, err := htmlutils.ValidateUrlAsArticle(articleUrl, r.env.PElementCharCount, r.env.PElementThreshold)
					if err != nil {
						slog.Error("[Article Repository] GetLatestArticlesFromUnsubscribed validate url as article:", "error", err)
						errCh <- err
						return
					}
					if !isArticle {
						slog.Warn("[Article Repository] GetLatestArticlesFromUnsubscribed: Skipping potential article: url is not an article:", "url", articleUrl)
						return
					}

					dbArticle, err := r.q.GetArticleByUrl(ctx, articleUrl)
					if err != nil {
						slog.Error("[Recsys Repository] GetLatestArticlesFromUnsubscribed query:", "error", err)
						errCh <- err
						return
					}
					dmArticle, err := r.completeDmArticleFromDb(ctx, &dbArticle)
					if err != nil {
						slog.Error("[Recsys Repository] GetLatestArticlesFromUnsubscribed convert:", "error", err)
						errCh <- err
						return
					}
					resCh <- dmArticle
				}(articleUrl)
			}

			// Wait for all goroutines to finish
			wg.Wait()

			// Close the channels after all goroutines finish
			close(errCh)
			close(resCh)

			// Check if there were any errors
			for err := range errCh {
				if err != nil {
					return nil, err
				}
			}

			// Collect all results
			for dmArticle := range resCh {
				dmArticles = append(dmArticles, dmArticle)
			}
		}
		return dmArticles, nil
	} else {
		dbArticles, err := r.q.GetAllArticles(ctx, store.GetAllArticlesParams{
			Count:  int32(count),
			Offset: int32(offset),
		})
		if err != nil {
			slog.Error("[Recsys Repository] GetLatestArticlesFromUnsubscribed query:", "error", err)
			return nil, err
		}

		var dmArticles []*domain.ArticleMetadata
		for _, dbArticle := range dbArticles {
			dmArticle, err := r.completeDmArticleFromDb(ctx, &dbArticle)
			if err != nil {
				slog.Error("[Article Repository] GetByPublisher convert:", "error", err)
				return nil, err
			}
			dmArticles = append(dmArticles, dmArticle)
		}
		return dmArticles, nil
	}
}

func (r *RecsysRepositoryImpl) GetLatestArticlesByPublisher(ctx context.Context, publisherId int32, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error) {
	// TODO
	return nil, nil
}

func (r *RecsysRepositoryImpl) GetRelatedArticles(ctx context.Context, articleId int64, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error) {
	thisArticle, err := r.q.GetArticleById(ctx, articleId)
	if err != nil {
		slog.Error("[Recsys Repository] GetRelatedArticles query:", "error", err)
		return nil, err
	}
	thisArticleUrl := thisArticle.Url

	articleUrls, err := r.engine.GetRelatedArticleUrlsFromUrl(thisArticleUrl, time.Duration(r.env.ContextTimeout)*time.Second)
	if err != nil {
		slog.Error("[Recsys Repository] GetRelatedArticles engine:", "error", err)
		return nil, err
	}

	var wg sync.WaitGroup
	dmArticles := make([]*domain.ArticleMetadata, 0)
	errCh := make(chan error, len(articleUrls))
	resCh := make(chan *domain.ArticleMetadata, len(articleUrls))

	for _, articleUrl := range articleUrls {
		wg.Add(1)
		go func(articleUrl string) {
			defer wg.Done()

			// Check if url is actually of an article
			isArticle, err := htmlutils.ValidateUrlAsArticle(articleUrl, r.env.PElementCharCount, r.env.PElementThreshold)
			if err != nil {
				slog.Error("[Article Repository] GetRelatedArticles validate url as article:", "error", err)
				errCh <- err
				return
			}
			if !isArticle {
				slog.Warn("[Article Repository] GetRelatedArticles: Skipping potential article: url is not an article:", "url", articleUrl)
				return
			}

			dbArticle, err := r.q.GetArticleByUrl(ctx, articleUrl)
			if err != nil {
				slog.Error("[Recsys Repository] GetRelatedArticles query:", "error", err)
				errCh <- err
				return
			}
			dmArticle, err := r.completeDmArticleFromDb(ctx, &dbArticle)
			if err != nil {
				slog.Error("[Recsys Repository] GetRelatedArticles convert:", "error", err)
				errCh <- err
				return
			}
			resCh <- dmArticle
		}(articleUrl)
	}

	// Wait for all goroutines to finish
	wg.Wait()

	// Close the channels after all goroutines finish
	close(errCh)
	close(resCh)

	// Check if there were any errors
	for err := range errCh {
		if err != nil {
			return nil, err
		}
	}

	// Collect all results
	for dmArticle := range resCh {
		dmArticles = append(dmArticles, dmArticle)
	}

	return dmArticles, nil
}
