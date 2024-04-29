package domain

import "time"

type Article struct {
	Id        uint32     `json:"id"`
	Title     string     `json:"title"`
	Publisher *Publisher `json:"publisher"`
	Date      time.Time  `json:"date"`
	Url       string     `json:"url"`
}

type ArticleRepository interface {
	GetById(id uint32) (*Article, error)
	GetByPublisher(publisherId uint32, count int) ([]*Article, error)
	GetLatest(count int) ([]*Article, error)
	Search(query string, count int) ([]*Article, error)
}
