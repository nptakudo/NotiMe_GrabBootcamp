package domain

import "time"

type ArticleMetadata struct {
	Id        uint32     `json:"id"`
	Title     string     `json:"title"`
	Publisher *Publisher `json:"publisher"`
	Date      time.Time  `json:"date"`
	Url       string     `json:"url"`
}

type ArticleRepository interface {
	GetById(id uint32) (*ArticleMetadata, error)
	GetByPublisher(publisherId uint32, count int, offset int) ([]*ArticleMetadata, error)
	GetLatest(count int, offset int) ([]*ArticleMetadata, error)
	Search(query string, count int, offset int) ([]*ArticleMetadata, error)
}
