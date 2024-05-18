package repository

import (
	"context"
	"log/slog"
	"notime/domain"
	"notime/external/sql/store"
)

type SubscribeListRepositoryImpl struct {
	q *store.Queries
}

func NewSubscribeListRepository(q *store.Queries) domain.SubscribeListRepository {
	return &SubscribeListRepositoryImpl{q: q}
}

func (r *SubscribeListRepositoryImpl) GetByUserId(ctx context.Context, userId int32) ([]*domain.Publisher, error) {
	dbSubscribePublishers, err := r.q.GetSubscribedPublishersByUserId(ctx, userId)
	if err != nil {
		return nil, err
	}
	dmSubscribePublishers := make([]*domain.Publisher, 0)
	for _, dbSubscribePublisher := range dbSubscribePublishers {
		dmSubscribePublisher, err := convertDbPublisherToDm(&dbSubscribePublisher)
		if err != nil {
			slog.Error("[SubscribeList Repository] GetByUserId:", "error", err)
			return nil, err
		}
		dmSubscribePublishers = append(dmSubscribePublishers, dmSubscribePublisher)
	}
	return dmSubscribePublishers, nil
}

func (r *SubscribeListRepositoryImpl) IsSubscribed(ctx context.Context, publisherId int32, userId int32) (bool, error) {
	_, err := r.q.IsPublisherSubscribedByUserId(ctx, store.IsPublisherSubscribedByUserIdParams{
		PublisherID: publisherId,
		UserID:      userId,
	})
	if err != nil {
		slog.Error("[SubscribeList Repository] IsSubscribed:", "error", err)
		return false, err
	}
	return true, nil
}

func (r *SubscribeListRepositoryImpl) AddToSubscribeList(ctx context.Context, publisherId int32, userId int32) error {
	err := r.q.SubscribePublisher(ctx, store.SubscribePublisherParams{
		PublisherID: publisherId,
		UserID:      userId,
	})
	if err != nil {
		slog.Error("[SubscribeList Repository] AddToSubscribeList:", "error", err)
		return err
	}
	return nil
}

func (r *SubscribeListRepositoryImpl) RemoveFromSubscribeList(ctx context.Context, publisherId int32, userId int32) error {
	err := r.q.UnsubscribePublisher(ctx, store.UnsubscribePublisherParams{
		PublisherID: publisherId,
		UserID:      userId,
	})
	if err != nil {
		slog.Error("[SubscribeList Repository] RemoveFromSubscribeList:", "error", err)
		return err
	}
	return nil
}
