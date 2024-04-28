package repository

import "notime/domain"

// ArticleRepositoryImpl TODO
type ArticleRepositoryImpl struct{}

func (r *ArticleRepositoryImpl) GetByID(id uint64) (*domain.Article, error) {
	return nil, nil
}

func (r *ArticleRepositoryImpl) GetByPublisher(publisher *domain.Publisher) ([]*domain.Article, error) {
	return nil, nil
}

func (r *ArticleRepositoryImpl) GetLatest() ([]*domain.Article, error) {
	return nil, nil
}

func (r *ArticleRepositoryImpl) GetLatestByPublisher(publisher *domain.Publisher) ([]*domain.Article, error) {
	return nil, nil
}

func (r *ArticleRepositoryImpl) GetRelated(article *domain.Article) ([]*domain.Article, error) {
	return nil, nil
}
