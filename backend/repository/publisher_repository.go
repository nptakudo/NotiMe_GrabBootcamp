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
	dbPublishers, err := r.q.SearchPublishersByName(ctx, sql.NullString{String: name, Valid: name != ""})
	if err != nil {
		slog.Error("[Publisher Repository] SearchByName:", "error", err)
		return nil, err
	}
	dmPublishers := make([]*domain.Publisher, 0)
	for _, dbPublisher := range dbPublishers {
		dmPublisher, err := convertDbPublisherToDm(&dbPublisher)
		if err != nil {
			slog.Error("[Publisher Repository] SearchByName:", "error", err)
			return nil, err
		}
		dmPublishers = append(dmPublishers, dmPublisher)
	}
	return dmPublishers, nil
}
func (r *PublisherRepositoryImpl) SearchByUrl(ctx context.Context, url string) (*domain.Publisher, error) {
	dbPublishers, err := r.q.SearchPublishersByUrl(ctx, sql.NullString{String: url, Valid: url != ""})
	if err != nil {
		slog.Error("[Publisher Repository] SearchByUrl:", "error", err)
		return nil, err
	}
	if len(dbPublishers) == 0 {
		slog.Error("[Publisher Repository] SearchByUrl:", "error", "Publisher not found")
		return nil, nil
	}
	dmPublisher, err := convertDbPublisherToDm(&dbPublishers[0])
	if err != nil {
		slog.Error("[Publisher Repository] SearchByUrl:", "error", err)
		return nil, err
	}
	return dmPublisher, nil
}

func (r *PublisherRepositoryImpl) Create(ctx context.Context, name string, url string, avatarUrl string) (*domain.Publisher, error) {
	// Add "/" to the end of the url if it doesn't have it
	if url[len(url)-1] != '/' {
		url += "/"
	}
	dbPublisher, err := r.q.CreatePublisher(ctx, store.CreatePublisherParams{
		Name:      name,
		Url:       url,
		AvatarUrl: sql.NullString{String: avatarUrl, Valid: avatarUrl != ""},
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
