package repository

import (
	"context"
	"log/slog"
	"notime/bootstrap"
	"notime/domain"
	"notime/external/sql/store"
	"notime/external/webscrape"
)

type RecsysRepository interface {
	GetLatestArticlesFromSubscribed(ctx context.Context, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error)
	GetLatestArticlesFromUnsubscribed(ctx context.Context, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error)
	GetLatestArticlesByPublisher(ctx context.Context, publisherId int32, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error)
	GetRelatedArticles(ctx context.Context, articleId int64, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error)
}

// RecsysRepositoryImpl TODO
type RecsysRepositoryImpl struct {
	q   *store.Queries
	env *bootstrap.Env
	UtilitiesRepository
}

func NewRecsysRepository(env *bootstrap.Env, q *store.Queries) RecsysRepository {
	return &RecsysRepositoryImpl{
		q:                   q,
		env:                 env,
		UtilitiesRepository: UtilitiesRepository{q: q},
	}
}

func (r *RecsysRepositoryImpl) GetLatestArticlesFromSubscribed(ctx context.Context, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error) {
	dbArticles, err := r.q.GetAllArticles(ctx)
	if err != nil {
		slog.Error("[Recsys Repository] GetLatestArticlesFromSubscribed query:", "error", err)
		return nil, err
	}

	var dmArticles []*domain.ArticleMetadata
	for _, dbArticle := range dbArticles {
		// Check if url is actually of an article
		isArticle, err := webscrape.ValidateUrlAsArticle(dbArticle.Url, r.env.PElementCharCount, r.env.PElementThreshold)
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
	return r.GetLatestArticlesFromSubscribed(ctx, userId, count, offset)
}

func (r *RecsysRepositoryImpl) GetLatestArticlesByPublisher(ctx context.Context, publisherId int32, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error) {
	return r.GetLatestArticlesFromSubscribed(ctx, userId, count, offset)
}

func (r *RecsysRepositoryImpl) GetRelatedArticles(ctx context.Context, articleId int64, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error) {
	return r.GetLatestArticlesFromSubscribed(ctx, userId, count, offset)
}
