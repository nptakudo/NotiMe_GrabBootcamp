package repository

import "notime/domain"

type RecsysRepository interface {
	GetLatestArticlesFromSubscribed(userId uint32, count int) ([]*domain.Article, error)
	GetLatestArticlesFromUnsubscribed(userId uint32, count int) ([]*domain.Article, error)
	GetLatestArticlesByPublisher(publisherId uint32, userId uint32, count int) ([]*domain.Article, error)
	GetRelatedArticles(articleId uint32, userId uint32, count int) ([]*domain.Article, error)
}

// RecsysRepositoryImpl TODO
type RecsysRepositoryImpl struct{}

func (r *RecsysRepositoryImpl) GetLatestArticlesFromSubscribed(userId uint32, count int) ([]*domain.Article, error) {
	return nil, nil
}

func (r *RecsysRepositoryImpl) GetLatestArticlesFromUnsubscribed(userId uint32, count int) ([]*domain.Article, error) {
	return nil, nil
}

func (r *RecsysRepositoryImpl) GetLatestArticlesByPublisher(publisherId uint32, userId uint32, count int) ([]*domain.Article, error) {
	return nil, nil
}

func (r *RecsysRepositoryImpl) GetRelatedArticles(articleId uint32, userId uint32, count int) ([]*domain.Article, error) {
	return nil, nil
}
