package repository

import "notime/domain"

// ArticleRepositoryImpl TODO
type ArticleRepositoryImpl struct{}

func (r *ArticleRepositoryImpl) GetByID(id uint32) (*domain.Article, error) {
	return nil, nil
}

func (r *ArticleRepositoryImpl) GetByPublisher(publisherId uint32) ([]*domain.Article, error) {
	return nil, nil
}

func (r *ArticleRepositoryImpl) GetLatest() ([]*domain.Article, error) {
	return nil, nil
}

func (r *ArticleRepositoryImpl) GetLatestByPublisher(publisherId uint32) ([]*domain.Article, error) {
	return nil, nil
}

func (r *ArticleRepositoryImpl) GetRelated(articleId uint32) ([]*domain.Article, error) {
	return nil, nil
}
