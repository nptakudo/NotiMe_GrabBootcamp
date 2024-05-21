package repository

import (
	"log/slog"
	"notime/bootstrap"
	"notime/domain"
	"notime/external/webscrape_engine"
	"notime/repository/models"
	"notime/utils/htmlutils"
	"sync"
	"time"
)

type WebscrapeEngine interface {
	ScrapeFromUrl(url string, timeout time.Duration) ([]*models.ScrapedArticle, error)
}

func NewWebscrapeEngine(host string, port string) WebscrapeEngine {
	return &webscrape_engine.WebscrapeEngineImpl{
		Host: host,
		Port: port,
	}
}

type WebscrapeRepository interface {
	ScrapeFromUrl(url string) ([]*domain.ArticleMetadata, *domain.Publisher, error)
}

type WebscrapeRepositoryImpl struct {
	env    *bootstrap.Env
	engine WebscrapeEngine
}

func NewWebscrapeRepository(env *bootstrap.Env) WebscrapeRepository {
	engine := NewWebscrapeEngine(env.WebscrapeHost, env.WebscrapePort)
	return &WebscrapeRepositoryImpl{env: env, engine: engine}
}

func (repo *WebscrapeRepositoryImpl) ScrapeFromUrl(url string) ([]*domain.ArticleMetadata, *domain.Publisher, error) {
	dmPublisher, err := htmlutils.CheckAndCompilePublisher(url, time.Duration(5)*time.Second)
	if err != nil {
		return nil, nil, err
	}

	scrapedArticles, err := repo.engine.ScrapeFromUrl(url, time.Duration(10)*time.Second)
	if err != nil {
		slog.Error("[Webscrape Repository] ScrapeFromUrl scrape from url:", "error", err)
		if scrapedArticles == nil || len(scrapedArticles) == 0 {
			return nil, nil, err
		}
	}

	var wg sync.WaitGroup
	dmArticles := make([]*domain.ArticleMetadata, 0)
	errCh := make(chan error, len(scrapedArticles))
	resCh := make(chan *domain.ArticleMetadata, len(scrapedArticles))

	for _, article := range scrapedArticles {
		wg.Add(1)
		go func(article *models.ScrapedArticle) {
			defer wg.Done()

			// Check if url is actually of an article
			isArticle, err := htmlutils.ValidateUrlAsArticle(article.Url, repo.env.PElementCharCount, repo.env.PElementThreshold)
			if err != nil {
				slog.Error("[Webscrape Repository] ScrapeFromUrl validate url as article:", "error", err)
				errCh <- err
				return
			}
			if !isArticle {
				slog.Warn("[Webscrape Repository] ScrapeFromUrl: Skipping potential article: url is not an article:", "url", article.Url)
				return
			}

			date, err := article.GetTime()
			if err != nil {
				slog.Error("[Webscrape Repository] ScrapeFromUrl get time:", "error", err)
				errCh <- err
				return
			}
			dmArticle := &domain.ArticleMetadata{
				Id:        -1,
				Title:     article.Title,
				Url:       article.Url,
				Date:      date,
				Publisher: dmPublisher,
			}
			resCh <- dmArticle
		}(article)
	}

	// Wait for all goroutines to finish
	wg.Wait()

	// Close the channels after all goroutines finish
	close(errCh)
	close(resCh)

	// Collect all results
	for dmArticle := range resCh {
		dmArticles = append(dmArticles, dmArticle)
	}

	return dmArticles, dmPublisher, nil
}
