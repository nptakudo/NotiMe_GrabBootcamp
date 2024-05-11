package repository

import (
	"context"
	"github.com/jackc/pgx/v5/pgtype"
	"log/slog"
	"notime/domain"
	"notime/external/sql/store"
)

// ArticleRepositoryImpl TODO
type ArticleRepositoryImpl struct {
	q *store.Queries
	UtilitiesRepository
}

func NewArticleRepository(q *store.Queries) domain.ArticleRepository {
	return &ArticleRepositoryImpl{q: q}
}

func (r *ArticleRepositoryImpl) GetById(ctx context.Context, id int64) (*domain.ArticleMetadata, error) {
	dbArticle, err := r.q.GetArticleById(ctx, id)
	if err != nil {
		slog.Error("[Article Repository] GetById query: %v", err)
		return nil, err
	}

	dmArticle, err := r.completeDmArticleFromDb(ctx, &dbArticle)
	if err != nil {
		slog.Error("[Article Repository] GetById convert: %v", err)
		return nil, err
	}
	return dmArticle, nil
}

func (r *ArticleRepositoryImpl) GetByPublisher(ctx context.Context, publisherId int32, count int, offset int) ([]*domain.ArticleMetadata, error) {
	dbArticles, err := r.q.GetArticlesByPublisherId(ctx, store.GetArticlesByPublisherIdParams{
		SourceID: pgtype.Int4{Int32: publisherId},
		Limit:    int32(count),
		Offset:   int32(offset),
	})
	if err != nil {
		slog.Error("[Article Repository] GetById query: %v", err)
		return nil, err
	}

	var dmArticles []*domain.ArticleMetadata
	for _, dbArticle := range dbArticles {
		dmArticle, err := r.completeDmArticleFromDb(ctx, &dbArticle)
		if err != nil {
			slog.Error("[Article Repository] GetById convert: %v", err)
			return nil, err
		}
		dmArticles = append(dmArticles, dmArticle)
	}
	return dmArticles, nil
}

func (r *ArticleRepositoryImpl) Search(ctx context.Context, query string, count int, offset int) ([]*domain.ArticleMetadata, error) {
	dbArticles, err := r.q.SearchArticlesByName(ctx, store.SearchArticlesByNameParams{
		Query:  pgtype.Text{String: query},
		Limit:  int32(count),
		Offset: int32(offset),
	})
	if err != nil {
		slog.Error("[Article Repository] GetById query: %v", err)
		return nil, err
	}

	var dmArticles []*domain.ArticleMetadata
	for _, dbArticle := range dbArticles {
		dmArticle, err := r.completeDmArticleFromDb(ctx, &dbArticle)
		if err != nil {
			slog.Error("[Article Repository] GetById convert: %v", err)
			return nil, err
		}
		dmArticles = append(dmArticles, dmArticle)
	}
	return dmArticles, nil
}
