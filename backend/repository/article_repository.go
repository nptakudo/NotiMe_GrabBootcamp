package repository

import "notime/domain"

// ArticleRepositoryImpl TODO
type ArticleRepositoryImpl struct{}

func (r *ArticleRepositoryImpl) GetById(id uint32) (*domain.ArticleMetadata, error) {
	return nil, nil
}

func (r *ArticleRepositoryImpl) GetByPublisher(publisherId uint32, count int, offset int) ([]*domain.ArticleMetadata, error) {
	return nil, nil
}

func (r *ArticleRepositoryImpl) GetLatest(count int, offset int) ([]*domain.ArticleMetadata, error) {
	return nil, nil
}

func (r *ArticleRepositoryImpl) Search(query string, count int, offset int) ([]*domain.ArticleMetadata, error) {
	return nil, nil
}
