package domain

import "time"

type Article struct {
	ID        uint64     `json:"id"`
	Title     string     `json:"title"`
	Content   string     `json:"content"`
	Publisher *Publisher `json:"publisher"`
	Date      time.Time  `json:"date"`
}

type ArticleRepository interface {
	GetByID(id uint64) (*Article, error)
	GetByPublisher(publisher *Publisher) ([]*Article, error)
	GetLatest() ([]*Article, error)
	GetLatestByPublisher(publisher *Publisher) ([]*Article, error)
	GetRelated(article *Article) ([]*Article, error)
}
