package mock_db

import (
	"context"
	"log/slog"
	"notime/bootstrap"
	"notime/external/sql/store"
	"notime/repository"
)

func Populate(ctx context.Context, env *bootstrap.Env, db *store.Queries) {
	slog.Info("[Populate] Start populating database")

	webscrape_repo := repository.NewWebscrapeRepository(env)
	article_repo := repository.NewArticleRepository(env, db)
	publisher_repo := repository.NewPublisherRepository(db)

	links := []string{
		"https://vnexpress.net/so-hoa",
		"https://thanhnien.vn/cong-nghe-game.htm",
		"https://www.bloomberg.com/technology",
		"https://security.apple.com/blog/",
		"https://engineering.grab.com",
		"https://discord.com/category/engineering",
	}

	publisher_count := 0
	article_count := 0
	for _, link := range links {
		articles, publisher, err := webscrape_repo.ScrapeFromUrl(link)
		if err != nil {
			slog.Error("[Populate] Failed to scrape from url:", "error", err)
			continue
		}

		dmPublisher, err := publisher_repo.Create(ctx, publisher.Name, publisher.Url, publisher.AvatarUrl)
		if err != nil {
			slog.Error("[Populate] Failed to create publisher:", "url", publisher.Url, "error", err)
			continue
		}
		slog.Info("[Populate] Created publisher:", "publisher name", dmPublisher.Name)
		publisher_count++

		for _, article := range articles {
			_, err = article_repo.Create(ctx, article.Title, article.Date, article.Url, dmPublisher.Id, "")
			if err != nil {
				slog.Error("[Populate] Failed to create article link ", "url", article.Url, "error", err)
				continue
			}
			slog.Info("[Populate] Created article:", "url", article.Url)
			article_count++
		}
	}
	slog.Info("[Populate] Finish!", "publisher count", publisher_count, "article count", article_count)
}
