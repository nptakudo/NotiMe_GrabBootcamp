package domain

import (
	"context"
	"time"
)

type ArticleMetadata struct {
	Id        int64      `json:"id"`
	Title     string     `json:"title"`
	Publisher *Publisher `json:"publisher"`
	Date      time.Time  `json:"date"`
	Url       string     `json:"url"`
}

type ArticleRepository interface {
	GetById(ctx context.Context, id int64) (*ArticleMetadata, error)
	GetByPublisher(ctx context.Context, publisherId int32, count int, offset int) ([]*ArticleMetadata, error)
	Search(ctx context.Context, query string, count int, offset int) ([]*ArticleMetadata, error)
}
