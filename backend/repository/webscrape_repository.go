package repository

import (
	"notime/bootstrap"
	"notime/domain"
	"notime/external/webscrape"
	"time"
)

type WebscrapeRepository interface {
	ScrapeFromUrl(url string) ([]*domain.ArticleMetadata, *domain.Publisher, error)
}

type WebscrapeRepositoryImpl struct {
	env *bootstrap.Env
}

func NewWebscrapeRepository(env *bootstrap.Env) WebscrapeRepository {
	return &WebscrapeRepositoryImpl{env: env}
}

func (repo *WebscrapeRepositoryImpl) ScrapeFromUrl(url string) ([]*domain.ArticleMetadata, *domain.Publisher, error) {
	dmPublisher, err := webscrape.CheckAndCompilePublisher(url, time.Duration(repo.env.ContextTimeout)*time.Second)
	if err != nil {
		return nil, nil, err
	}

	scrapedArticles, err := webscrape.ScrapeFromUrl(url, repo.env.WebscrapeHost, repo.env.WebscrapePort, time.Duration(repo.env.ContextTimeout)*time.Second)
	if err != nil {
		return nil, nil, err
	}

	dmArticles := make([]*domain.ArticleMetadata, 0)
	for _, article := range scrapedArticles {
		dmArticle := &domain.ArticleMetadata{
			Id:        -1,
			Title:     article.Title,
			Url:       article.Url,
			Date:      article.Date,
			Publisher: dmPublisher,
		}
		dmArticles = append(dmArticles, dmArticle)
	}
	return dmArticles, dmPublisher, nil
}
