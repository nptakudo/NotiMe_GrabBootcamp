package repository

import "notime/domain"

type RecsysRepository interface {
	GetLatestArticlesFromSubscribed(userId uint32, count int) ([]*domain.ArticleMetadata, error)
	GetLatestArticlesFromUnsubscribed(userId uint32, count int) ([]*domain.ArticleMetadata, error)
	GetLatestArticlesByPublisher(publisherId uint32, userId uint32, count int) ([]*domain.ArticleMetadata, error)
	GetRelatedArticles(articleId uint32, userId uint32, count int) ([]*domain.ArticleMetadata, error)
}

// RecsysRepositoryImpl TODO
type RecsysRepositoryImpl struct{}

func (r *RecsysRepositoryImpl) GetLatestArticlesFromSubscribed(userId uint32, count int) ([]*domain.ArticleMetadata, error) {
	return nil, nil
}

func (r *RecsysRepositoryImpl) GetLatestArticlesFromUnsubscribed(userId uint32, count int) ([]*domain.ArticleMetadata, error) {
	return nil, nil
}

func (r *RecsysRepositoryImpl) GetLatestArticlesByPublisher(publisherId uint32, userId uint32, count int) ([]*domain.ArticleMetadata, error) {
	return nil, nil
}

func (r *RecsysRepositoryImpl) GetRelatedArticles(articleId uint32, userId uint32, count int) ([]*domain.ArticleMetadata, error) {
	return nil, nil
}
