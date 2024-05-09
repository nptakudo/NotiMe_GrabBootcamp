package usecases

import (
	"context"
	"log/slog"
	"notime/api/controller"
	"notime/api/messages"
	"notime/bootstrap"
	"notime/domain"
	"notime/external/sql/store"
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

func NewReaderUsecase(env *bootstrap.Env, db *store.Queries) controller.ReaderUsecase {
	bookmarkListRepository := repository.NewBookmarkListRepository(db)
	recsysRepository := repository.NewRecsysRepository()

	return &ReaderUsecaseImpl{
		env:                    env,
		CommonUsecase:          NewCommonUsecase(db),
		RecsysRepository:       recsysRepository,
		BookmarkListRepository: bookmarkListRepository,
	}
}

func (uc *ReaderUsecaseImpl) GetArticleById(ctx context.Context, id int64, userId int32) (*messages.ArticleResponse, error) {
	metadata, err := uc.CommonUsecase.GetArticleMetadataById(ctx, id, userId)
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

func (uc *ReaderUsecaseImpl) GetRelatedArticles(ctx context.Context, articleId int64, userId int32, count int, offset int) (*messages.RelatedArticlesResponse, error) {
	relatedArticlesDm, err := uc.RecsysRepository.GetRelatedArticles(ctx, articleId, userId, count, offset)
	if err != nil {
		slog.Error("[ReaderUsecase] GetRelatedArticles: %v", err)
		return nil, ErrInternal
	}
	relatedArticlesApi, err := fromDmArticlesToApi(ctx, relatedArticlesDm, userId, uc.BookmarkListRepository)
	if err != nil {
		slog.Error("[ReaderUsecase] GetRelatedArticles: %v", err)
		return nil, ErrInternal
	}
	return &messages.RelatedArticlesResponse{Articles: relatedArticlesApi}, nil
}
