package repository

import (
	"context"
	"database/sql"
	"log/slog"
	"notime/domain"
	"notime/external/sql/store"
)

type PublisherRepositoryImpl struct {
	q *store.Queries
}

func NewPublisherRepository(q *store.Queries) domain.PublisherRepository {
	return &PublisherRepositoryImpl{q: q}
}

func (r *PublisherRepositoryImpl) GetById(ctx context.Context, id int32) (*domain.Publisher, error) {
	dbPublisher, err := r.q.GetPublisherById(ctx, id)
	if err != nil {
		slog.Error("[Publisher Repository] GetById:", "error", err)
		return nil, err
	}
	dmPublisher, err := convertDbPublisherToDm(&dbPublisher)
	if err != nil {
		slog.Error("[Publisher Repository] GetById:", "error", err)
		return nil, err
	}
	return dmPublisher, nil
}

func (r *PublisherRepositoryImpl) SearchByName(ctx context.Context, name string) ([]*domain.Publisher, error) {
	dbPublishers, err := r.q.SearchPublishersByName(ctx, name)
	if err != nil {
		slog.Error("[Publisher Repository] Search:", "error", err)
		return nil, err
	}
	dmPublishers := make([]*domain.Publisher, 0)
	for _, dbPublisher := range dbPublishers {
		dmPublisher, err := convertDbPublisherToDm(&dbPublisher)
		if err != nil {
			slog.Error("[Publisher Repository] Search:", "error", err)
			return nil, err
		}
		dmPublishers = append(dmPublishers, dmPublisher)
	}
	return dmPublishers, nil
}
func (r *PublisherRepositoryImpl) SearchByUrl(ctx context.Context, url string) (*domain.Publisher, error) {
	dbPublishers, err := r.q.SearchPublishersByUrl(ctx, url)
	if err != nil {
		slog.Error("[Publisher Repository] Search:", "error", err)
		return nil, err
	}
	if len(dbPublishers) == 0 {
		slog.Error("[Publisher Repository] Search:", "error", "Publisher not found")
		return nil, nil
	}
	dmPublisher, err := convertDbPublisherToDm(&dbPublishers[0])
	if err != nil {
		slog.Error("[Publisher Repository] Search:", "error", err)
		return nil, err
	}
	return dmPublisher, nil
}

func (r *PublisherRepositoryImpl) Create(ctx context.Context, name string, url string, avatarUrl string) (*domain.Publisher, error) {
	dbPublisher, err := r.q.CreatePublisher(ctx, store.CreatePublisherParams{
		Name:      name,
		Url:       url,
		AvatarUrl: sql.NullString{avatarUrl, avatarUrl != ""},
	})
	if err != nil {
		slog.Error("[Publisher Repository] Create:", "error", err)
		return nil, err
	}
	dmPublisher, err := convertDbPublisherToDm(&dbPublisher)
	if err != nil {
		slog.Error("[Publisher Repository] Create:", "error", err)
		return nil, err
	}
	return dmPublisher, nil
}
