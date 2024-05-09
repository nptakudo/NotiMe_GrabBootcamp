package repository

import (
	"context"
	"notime/domain"
)

type RecsysRepository interface {
	GetLatestArticlesFromSubscribed(ctx context.Context, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error)
	GetLatestArticlesFromUnsubscribed(ctx context.Context, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error)
	GetLatestArticlesByPublisher(ctx context.Context, publisherId int32, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error)
	GetRelatedArticles(ctx context.Context, articleId int64, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error)
}

// RecsysRepositoryImpl TODO
type RecsysRepositoryImpl struct{}

func NewRecsysRepository() RecsysRepository {
	return &RecsysRepositoryImpl{}
}

func (r *RecsysRepositoryImpl) GetLatestArticlesFromSubscribed(ctx context.Context, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error) {
	return nil, nil
}

func (r *RecsysRepositoryImpl) GetLatestArticlesFromUnsubscribed(ctx context.Context, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error) {
	return nil, nil
}

func (r *RecsysRepositoryImpl) GetLatestArticlesByPublisher(ctx context.Context, publisherId int32, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error) {
	return nil, nil
}

func (r *RecsysRepositoryImpl) GetRelatedArticles(ctx context.Context, articleId int64, userId int32, count int, offset int) ([]*domain.ArticleMetadata, error) {
	return nil, nil
}
