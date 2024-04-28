package models

import "notime/domain"

type ArticlesRequest struct {
	Count  int    `form:"count" binding:"required"`
	UserId uint32 `form:"user_id" binding:"required"`
}

type SearchRequest struct {
	Query  string `form:"query" binding:"required"`
	Count  int    `form:"count" binding:"required"`
	UserId uint32 `form:"user_id" binding:"required"`
}

type ArticlesResponse struct {
	Articles []*domain.Article `json:"articles"`
}

type HomeUsecase interface {
	GetSubscribedArticlesByDate(count int, userId uint32) ([]*domain.Article, error)
	GetSubscribedArticlesByPublisher(countEachPublisher int, userId uint32) ([]*domain.Article, error)
	GetExploreArticles(count int, userId uint32) ([]*domain.Article, error)
	Search(query string, count int, userId uint32) ([]*domain.Article, error)
}
