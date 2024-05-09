package repository

import "notime/domain"

// PublisherRepositoryImpl TODO
type PublisherRepositoryImpl struct{}

func (r *PublisherRepositoryImpl) GetById(id uint32) (*domain.Publisher, error) {
	return nil, nil
}

func (r *PublisherRepositoryImpl) Search(name string) ([]*domain.Publisher, error) {
	return nil, nil
}
