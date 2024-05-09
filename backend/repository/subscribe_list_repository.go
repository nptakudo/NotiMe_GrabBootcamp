package repository

import "notime/domain"

// SubscribeListRepositoryImpl TODO
type SubscribeListRepositoryImpl struct{}

func (r *SubscribeListRepositoryImpl) GetByUserId(userId uint32) ([]*domain.Publisher, error) {
	return nil, nil
}

func (r *SubscribeListRepositoryImpl) IsSubscribed(publisherId uint32, userId uint32) (bool, error) {
	return false, nil
}

func (r *SubscribeListRepositoryImpl) AddToSubscribeList(publisherId uint32, userId uint32) error {
	return nil
}

func (r *SubscribeListRepositoryImpl) RemoveFromSubscribeList(publisherId uint32, userId uint32) error {
	return nil
}
