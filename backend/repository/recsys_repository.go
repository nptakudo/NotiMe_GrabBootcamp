package repository

import (
	"context"
	"log/slog"
	"notime/bootstrap"
	"notime/domain"
	"notime/external/recsys_engine"
	"notime/external/sql/store"
	"notime/utils/htmlutils"
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

	var dmArticles []*domain.ArticleMetadata
	for _, dbArticle := range dbArticles {
		// Check if url is actually of an article
		isArticle, err := htmlutils.ValidateUrlAsArticle(dbArticle.Url, r.env.PElementCharCount, r.env.PElementThreshold)
		if err != nil {
			slog.Error("[Article Repository] GetByPublisher validate url as article:", "error", err)
			continue
		}
		if !isArticle {
			slog.Warn("[Article Repository] GetByPublisher: Skipping potential article: url is not an article:", "url", dbArticle.Url)
			continue
		}

		dmArticle, err := r.completeDmArticleFromDb(ctx, &dbArticle)
		if err != nil {
			slog.Error("[Article Repository] GetByPublisher convert:", "error", err)
			return nil, err
		}
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

	var dmArticles []*domain.ArticleMetadata
	for _, dbPublisher := range dbSubscribedPublishers {
		articleUrls, err := r.engine.GetRelatedArticleUrlsFromUrl(dbPublisher.Url, time.Duration(r.env.ContextTimeout)*time.Second)
		if err != nil {
			slog.Error("[Recsys Repository] GetLatestArticlesFromUnsubscribed engine:", "error", err)
			continue
		}

		for _, articleUrl := range articleUrls {
			// Check if url is actually of an article
			isArticle, err := htmlutils.ValidateUrlAsArticle(articleUrl, r.env.PElementCharCount, r.env.PElementThreshold)
			if err != nil {
				slog.Error("[Article Repository] GetLatestArticlesFromUnsubscribed validate url as article:", "error", err)
				continue
			}
			if !isArticle {
				slog.Warn("[Article Repository] GetLatestArticlesFromUnsubscribed: Skipping potential article: url is not an article:", "url", articleUrl)
				continue
			}

			dbArticle, err := r.q.GetArticleByUrl(ctx, articleUrl)
			if err != nil {
				slog.Error("[Recsys Repository] GetLatestArticlesFromUnsubscribed query:", "error", err)
				continue
			}
			dmArticle, err := r.completeDmArticleFromDb(ctx, &dbArticle)
			if err != nil {
				slog.Error("[Recsys Repository] GetLatestArticlesFromUnsubscribed convert:", "error", err)
				continue
			}
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

	articleUrls, err := r.engine.GetRelatedArticleUrlsFromUrl(thisArticleUrl, time.Duration(r.env.ContextTimeout)*time.Second)
	if err != nil {
		slog.Error("[Recsys Repository] GetRelatedArticles engine:", "error", err)
		return nil, err
	}

	var dmArticles []*domain.ArticleMetadata
	for _, articleUrl := range articleUrls {
		// Check if url is actually of an article
		isArticle, err := htmlutils.ValidateUrlAsArticle(articleUrl, r.env.PElementCharCount, r.env.PElementThreshold)
		if err != nil {
			slog.Error("[Article Repository] GetRelatedArticles validate url as article:", "error", err)
			continue
		}
		if !isArticle {
			slog.Warn("[Article Repository] GetRelatedArticles: Skipping potential article: url is not an article:", "url", articleUrl)
			continue
		}

		dbArticle, err := r.q.GetArticleByUrl(ctx, articleUrl)
		if err != nil {
			slog.Error("[Recsys Repository] GetRelatedArticles query:", "error", err)
			continue
		}
		dmArticle, err := r.completeDmArticleFromDb(ctx, &dbArticle)
		if err != nil {
			slog.Error("[Recsys Repository] GetRelatedArticles convert:", "error", err)
			continue
		}
		dmArticles = append(dmArticles, dmArticle)
	}

	return dmArticles, nil
}
