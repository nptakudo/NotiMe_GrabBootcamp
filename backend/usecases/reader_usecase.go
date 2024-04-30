package usecases

import (
	"log/slog"
	"notime/api/controller"
	"notime/api/messages"
	"notime/bootstrap"
	"notime/domain"
	"notime/repository"
	"notime/utils/geminiutils"
	"notime/utils/htmlutils"
)

type ReaderUsecaseImpl struct {
	env                    *bootstrap.Env
	CommonUsecase          controller.CommonUsecase
	RecsysRepository       repository.RecsysRepository
	BookmarkListRepository domain.BookmarkListRepository
}

func (uc *ReaderUsecaseImpl) GetArticleById(id uint32, userId uint32) (*messages.ArticleResponse, error) {
	metadata, err := uc.CommonUsecase.GetArticleMetadataById(id, userId)
	if err != nil {
		return nil, err
	}

	// Scrape article content & primary image
	body, err := htmlutils.ScrapeAndConvertArticleToMarkdown(metadata.Url)
	if err != nil {
		slog.Error("[ReaderUsecase] GetArticleById: %v", err)
		body = ""
	}
	imgSrc, err := htmlutils.GetLargestImageUrlFromArticle(metadata.Url)
	if err != nil {
		slog.Error("[ReaderUsecase] GetArticleById: %v", err)
		imgSrc = ""
	}

	// Generate article summary
	summary, err := geminiutils.GenerateArticleSummary(uc.env, body)
	if err != nil {
		slog.Error("[ReaderUsecase] GetArticleById: %v", err)
		summary = ""
	}

	return &messages.ArticleResponse{
		Metadata: metadata,
		Content: &messages.ArticleContent{
			Id:       metadata.Id,
			Content:  body,
			ImageUrl: imgSrc,
		},
		Summary: summary,
	}, nil
}

func (uc *ReaderUsecaseImpl) GetRelatedArticles(articleId uint32, userId uint32, count int, page int) (*messages.RelatedArticlesResponse, error) {
	relatedArticlesDm, err := uc.RecsysRepository.GetRelatedArticles(articleId, userId, count, page)
	if err != nil {
		slog.Error("[ReaderUsecase] GetRelatedArticles: %v", err)
		return nil, ErrInternal
	}
	relatedArticlesApi, err := fromDmArticlesToApi(relatedArticlesDm, userId, uc.BookmarkListRepository)
	if err != nil {
		slog.Error("[ReaderUsecase] GetRelatedArticles: %v", err)
		return nil, ErrInternal
	}
	return &messages.RelatedArticlesResponse{Articles: relatedArticlesApi}, nil
}
