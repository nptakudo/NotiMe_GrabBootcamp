package domain

import "time"

type Article struct {
	ID        uint32     `json:"id"`
	Title     string     `json:"title"`
	Publisher *Publisher `json:"publisher"`
	Date      time.Time  `json:"date"`
}

type ArticleRepository interface {
	GetByID(id uint32) (*Article, error)
	GetByPublisher(publisherId uint32) ([]*Article, error)
	GetLatest() ([]*Article, error)
	GetLatestByPublisher(publisherId uint32) ([]*Article, error)
	GetRelated(articleId uint32) ([]*Article, error)
}
