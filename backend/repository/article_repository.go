package repository

import (
	"context"
	"database/sql"
	"log/slog"
	"notime/bootstrap"
	"notime/domain"
	"notime/external/sql/store"
	"notime/utils/htmlutils"
	"time"
)

type ArticleRepositoryImpl struct {
	q   *store.Queries
	env *bootstrap.Env
	UtilitiesRepository
}

func NewArticleRepository(env *bootstrap.Env, q *store.Queries) domain.ArticleRepository {
	return &ArticleRepositoryImpl{
		q:                   q,
		env:                 env,
		UtilitiesRepository: UtilitiesRepository{q: q},
	}
}

func (r *ArticleRepositoryImpl) GetById(ctx context.Context, id int64) (*domain.ArticleMetadata, error) {
	dbArticle, err := r.q.GetArticleById(ctx, id)
	if err != nil {
		slog.Error("[Article Repository] GetById query:", "error", err)
		return nil, err
	}

	dmArticle, err := r.completeDmArticleFromDb(ctx, &dbArticle)
	if err != nil {
		slog.Error("[Article Repository] GetById convert:", "error", err)
		return nil, err
	}
	return dmArticle, nil
}

func (r *ArticleRepositoryImpl) GetByPublisher(ctx context.Context, publisherId int32, count int, offset int) ([]*domain.ArticleMetadata, error) {
	dbArticles, err := r.q.GetArticlesByPublisherId(ctx, store.GetArticlesByPublisherIdParams{
		PublisherID: publisherId,
		Count:       int32(count),
		Offset:      int32(offset),
	})
	if err != nil {
		slog.Error("[Article Repository] GetByPublisher query:", "error", err)
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

func (r *ArticleRepositoryImpl) Search(ctx context.Context, query string, count int, offset int) ([]*domain.ArticleMetadata, error) {
	dbArticles, err := r.q.SearchArticlesByName(ctx, store.SearchArticlesByNameParams{
		Query:  sql.NullString{String: query, Valid: true},
		Count:  int32(count),
		Offset: int32(offset),
	})
	if err != nil {
		slog.Error("[Article Repository] Search query:", "error", err)
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
			slog.Error("[Article Repository] Search convert:", "error", err)
			return nil, err
		}
		dmArticles = append(dmArticles, dmArticle)
	}
	return dmArticles, nil
}

func (r *ArticleRepositoryImpl) Create(ctx context.Context, title string, publishDate time.Time, url string, publisherId int32) (*domain.ArticleMetadata, error) {
	dbArticle, err := r.q.CreateArticle(ctx, store.CreateArticleParams{
		Title:       title,
		PublishDate: publishDate,
		Url:         url,
		PublisherID: publisherId,
	})
	if err != nil {
		slog.Error("[Article Repository] Create:", "error", err)
		return nil, err
	}
	slog.Info("[Article Repository] Create:", "article id", dbArticle.ID)

	dmArticle, err := r.completeDmArticleFromDb(ctx, &dbArticle)
	if err != nil {
		slog.Error("[Article Repository] Create:", "error", err)
		return nil, err
	}
	return dmArticle, nil
}
