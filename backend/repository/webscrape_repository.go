package repository

import (
	"log/slog"
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
		slog.Error("[Webscrape Repository] ScrapeFromUrl scrape from url:", "error", err)
		if scrapedArticles == nil || len(scrapedArticles) == 0 {
			return nil, nil, err
		}
	}

	dmArticles := make([]*domain.ArticleMetadata, 0)
	for _, article := range scrapedArticles {
		// Check if url is actually of an article
		isArticle, err := webscrape.ValidateUrlAsArticle(article.Url, repo.env.PElementCharCount, repo.env.PElementThreshold)
		if err != nil {
			slog.Error("[Webscrape Repository] ScrapeFromUrl validate url as article:", "error", err)
			continue
		}
		if !isArticle {
			slog.Warn("[Webscrape Repository] ScrapeFromUrl: Skipping potential article: url is not an article:", "url", article.Url)
			continue
		}

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
