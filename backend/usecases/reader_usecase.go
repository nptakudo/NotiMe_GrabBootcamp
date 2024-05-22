package usecases

import (
	"context"
	"encoding/json"
	"io"
	"log/slog"
	"notime/api/controller"
	"notime/api/messages"
	"notime/bootstrap"
	"notime/domain"
	"notime/external/sql/store"
	"notime/repository"
	"notime/repository/models"
	"notime/utils/geminiutils"
	"notime/utils/htmlutils"
	"os"
	"time"
)

type ReaderUsecaseImpl struct {
	env                    *bootstrap.Env
	CommonUsecase          controller.CommonUsecase
	RecsysRepository       repository.RecsysRepository
	BookmarkListRepository domain.BookmarkListRepository
}

func NewReaderUsecase(env *bootstrap.Env, db *store.Queries) controller.ReaderUsecase {
	bookmarkListRepository := repository.NewBookmarkListRepository(env, db)
	recsysRepository := repository.NewRecsysRepository(env, db)

	return &ReaderUsecaseImpl{
		env:                    env,
		CommonUsecase:          NewCommonUsecase(env, db),
		RecsysRepository:       recsysRepository,
		BookmarkListRepository: bookmarkListRepository,
	}
}

func (uc *ReaderUsecaseImpl) GetArticleById(ctx context.Context, id int64, userId int32) (*messages.ArticleResponse, error) {
	metadata, err := uc.CommonUsecase.GetArticleMetadataById(ctx, id, userId)
	if err != nil {
		return nil, err
	}

	// Scrape article content
	body, err := htmlutils.ScrapeAndConvertArticleToMarkdown(metadata.Url, time.Duration(uc.env.ContextTimeout)*time.Second)
	if err != nil {
		slog.Error("[ReaderUsecase] GetArticleById:", "error", err)
		body = ""
	}

	// Generate article summary
	summary := ""
	if body != "" {
		summary, err = geminiutils.GenerateArticleSummary(uc.env, body)
		if err != nil {
			slog.Error("[ReaderUsecase] GetArticleById:", "error", err)
			summary = ""
		}
	}
	if summary == "" && metadata.RawContent != "" {
		summary, err = geminiutils.GenerateArticleSummary(uc.env, metadata.RawContent)
		if err != nil {
			slog.Error("[ReaderUsecase] GetArticleById:", "error", err)
			summary = ""
		}
	}

	return &messages.ArticleResponse{
		Metadata: metadata,
		Content: &messages.ArticleContent{
			Id:      metadata.Id,
			Content: body,
		},
		Summary: summary,
	}, nil
}

func (uc *ReaderUsecaseImpl) GetRelatedArticles(ctx context.Context, articleId int64, userId int32, count int, offset int) ([]*messages.ArticleMetadata, error) {
	relatedArticlesDm, err := uc.RecsysRepository.GetRelatedArticles(ctx, articleId, userId, count, offset)
	if err != nil {
		slog.Error("[ReaderUsecase] GetRelatedArticles:", "error", err)
		return nil, ErrInternal
	}
	relatedArticlesApi, err := fromDmArticlesToApi(ctx, relatedArticlesDm, userId, uc.BookmarkListRepository)
	if err != nil {
		slog.Error("[ReaderUsecase] GetRelatedArticles:", "error", err)
		return nil, ErrInternal
	}
	return relatedArticlesApi, nil
}

func (uc *ReaderUsecaseImpl) GetNewArticle(ctx context.Context, url string) (*messages.ArticleResponse, error) {
	// get metadata by finding the url in the output.json file
	file, err := os.Open("../pipeline/webscrape/webscrape/spiders/output.json")
	if err != nil {
		slog.Error("[ReaderUsecase] GetNewArticle: Error opening output.json:", "error", err)
		return nil, ErrInternal
	}
	defer file.Close()

	// Read the file content
	content, err := io.ReadAll(file)
	if err != nil {
		slog.Error("[ReaderUsecase] GetNewArticle: Error reading output.json:", "error", err)
		return nil, ErrInternal
	}

	// Decode the response
	var articles []*models.ScrapedArticle
	err = json.Unmarshal(content, &articles)
	if err != nil {
		slog.Error("[Webscrape] ScrapeFromUrl: Error unmarshalling output.json:", "error", err)
		return nil, err
	}
	// get the article with the given url
	var article *models.ScrapedArticle
	for _, a := range articles {
		if a.Url == url {
			article = a
			break
		}
	}

	if article == nil {
		slog.Error("[ReaderUsecase] GetNewArticle: Article not found")
		return nil, ErrInternal
	}

	body, err := htmlutils.ScrapeAndConvertArticleToMarkdown(url, time.Duration(uc.env.ContextTimeout)*time.Second)
	if err != nil {
		slog.Error("[ReaderUsecase] GetNewArticle:", "error", err)
		body = ""
	}

	// Generate article summary
	summary := ""
	if body != "" {
		summary, err = geminiutils.GenerateArticleSummary(uc.env, body)
		if err != nil {
			slog.Error("[ReaderUsecase] GetArticleById:", "error", err)
			summary = ""
		}
	}
	if summary == "" && article.Content != "" {
		summary, err = geminiutils.GenerateArticleSummary(uc.env, article.Content)
		if err != nil {
			slog.Error("[ReaderUsecase] GetArticleById:", "error", err)
			summary = ""
		}
	}

	publishDate, err := article.GetTime()
	if err != nil {
		slog.Error("[ReaderUsecase] GetArticleById: get time:", "error", err)
		publishDate = time.Now().UTC()
	}
	slog.Info("[ReaderUsecase] GetNewArticle:", "publishDate", publishDate)

	metadata := domain.ArticleMetadata{
		Id:    0,
		Title: article.Title,
		Publisher: &domain.Publisher{
			Id:        0,
			Name:      article.PublisherName,
			Url:       "",
			AvatarUrl: "",
		},
		Date:     publishDate,
		Url:      article.Url,
		ImageUrl: "",
	}

	return &messages.ArticleResponse{
		Metadata: &messages.ArticleMetadata{
			IsBookmarked:    false,
			ArticleMetadata: metadata,
		},
		Content: &messages.ArticleContent{
			Id:      metadata.Id,
			Content: body,
		},
		Summary: summary,
	}, nil
}
