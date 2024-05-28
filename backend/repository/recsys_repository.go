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
		UtilitiesRepository: UtilitiesRepository{q: q, env: env},
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
			isArticle, err := htmlutils.ValidateUrlAsArticle(dbArticle.Url, r.env.PElementCharCount, r.env.PElementThreshold, time.Duration(r.env.ContextTimeout)*time.Second)
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
		dbSubscribedPublishers = nil
	}

	dmArticles := make([]*domain.ArticleMetadata, 0)

	if dbSubscribedPublishers != nil && len(dbSubscribedPublishers) > 0 {
		var wg sync.WaitGroup
		errCh := make(chan error, len(dbSubscribedPublishers))
		resCh := make(chan []*domain.ArticleMetadata, len(dbSubscribedPublishers))

		for _, dbPublisher := range dbSubscribedPublishers {
			wg.Add(1)
			go func(dbPublisher store.Source) {
				defer wg.Done()

				articleUrls, err := r.engine.GetRelatedArticleUrlsFromUrl(dbPublisher.Url, time.Duration(10)*time.Second)
				if err != nil {
					slog.Error("[Recsys Repository] GetLatestArticlesFromUnsubscribed engine:", "error", err)
					errCh <- err
					return
				}

				var wgInner sync.WaitGroup
				errChInner := make(chan error, len(articleUrls))
				resChInner := make(chan *domain.ArticleMetadata, len(articleUrls))

				for _, articleUrl := range articleUrls {
					wgInner.Add(1)
					go func(articleUrl string) {
						defer wgInner.Done()

						dbArticle, err := r.q.GetArticleByUrl(ctx, articleUrl)
						if err != nil {
							slog.Error("[Recsys Repository] GetLatestArticlesFromUnsubscribed query:", "error", err)
							errChInner <- err
							return
						}
						dmArticle, err := r.completeDmArticleFromDb(ctx, &dbArticle)
						if err != nil {
							slog.Error("[Recsys Repository] GetLatestArticlesFromUnsubscribed convert:", "error", err)
							errChInner <- err
							return
						}
						resChInner <- dmArticle
					}(articleUrl)
				}

				// Wait for all inner goroutines to finish
				wgInner.Wait()

				// Close the inner channels after all inner goroutines finish
				close(errChInner)
				close(resChInner)

				// Collect all inner results
				dmArticlesInner := make([]*domain.ArticleMetadata, 0)
				for dmArticle := range resChInner {
					dmArticlesInner = append(dmArticlesInner, dmArticle)
				}

				// Send the inner results to the outer result channel
				resCh <- dmArticlesInner
			}(dbPublisher)
		}

		// Wait for all outer goroutines to finish
		wg.Wait()

		// Close the outer channels after all outer goroutines finish
		close(errCh)
		close(resCh)

		// Collect all outer results
		for dmArticlesOuter := range resCh {
			dmArticles = append(dmArticles, dmArticlesOuter...)
		}
	}
	if len(dmArticles) < count {
		count -= len(dmArticles)
		dbArticles, err := r.q.GetRandomArticles(ctx, store.GetRandomArticlesParams{
			Count:  int32(count),
			Offset: int32(offset),
		})
		if err != nil {
			slog.Error("[Recsys Repository] GetLatestArticlesFromUnsubscribed query:", "error", err)
			return nil, err
		}

		var wg sync.WaitGroup
		errCh := make(chan error, len(dbArticles))
		resCh := make(chan *domain.ArticleMetadata, len(dbArticles))

		for _, dbArticle := range dbArticles {
			wg.Add(1)
			go func(dbArticle store.Post) {
				defer wg.Done()

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

		// Collect all results
		for dmArticle := range resCh {
			dmArticles = append(dmArticles, dmArticle)
		}
	}

	return dmArticles, nil
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

	articleUrls, err := r.engine.GetRelatedArticleUrlsFromUrl(thisArticleUrl, time.Duration(10)*time.Second)
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
			isArticle, err := htmlutils.ValidateUrlAsArticle(articleUrl, r.env.PElementCharCount, r.env.PElementThreshold, time.Duration(r.env.ContextTimeout)*time.Second)
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

	// Collect all results
	for dmArticle := range resCh {
		dmArticles = append(dmArticles, dmArticle)
	}

	return dmArticles, nil
}
