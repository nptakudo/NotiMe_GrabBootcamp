package repository

import "notime/domain"

// ArticleRepositoryImpl TODO
type ArticleRepositoryImpl struct{}

func (r *ArticleRepositoryImpl) GetByID(id uint32) (*domain.Article, error) {
	return nil, nil
}

func (r *ArticleRepositoryImpl) GetByPublisher(publisherId uint32, count int) ([]*domain.Article, error) {
	return nil, nil
}

func (r *ArticleRepositoryImpl) GetLatest(count int) ([]*domain.Article, error) {
	return nil, nil
}

func (r *ArticleRepositoryImpl) Search(query string, count int) ([]*domain.Article, error) {
	return nil, nil
}
