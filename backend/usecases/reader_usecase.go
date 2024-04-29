package usecases

import (
	"log/slog"
	"notime/api/messages"
	"notime/domain"
	"notime/repository"
	"notime/utils/htmlutils"
)

type ReaderUsecaseImpl struct {
	CommonUsecase          CommonUsecase
	RecsysRepository       repository.RecsysRepository
	BookmarkListRepository domain.BookmarkListRepository
}

func (uc *ReaderUsecaseImpl) GetArticleById(id uint32, userId uint32) (*messages.ArticleResponse, error) {
	metadata, err := uc.CommonUsecase.GetArticleMetadataById(id, userId)
	if err != nil {
		return nil, err
	}
	body, err := htmlutils.ScrapeAndConvertArticleToMarkdown(metadata.Url)
	if err != nil {
		slog.Error("[ReaderUsecase] GetArticleById: %v", err)
		return nil, ErrInternal
	}
	imgSrc, err := htmlutils.GetLargestImageUrlFromArticle(metadata.Url)
	if err != nil {
		slog.Error("[ReaderUsecase] GetArticleById: %v", err)
		return nil, ErrInternal
	}
	return &messages.ArticleResponse{
		Metadata: metadata,
		Content: &messages.ArticleContent{
			Id:       metadata.Id,
			Content:  body,
			ImageUrl: imgSrc,
		},
	}, nil
}

func (uc *ReaderUsecaseImpl) Bookmark(bookmarkListId uint32, articleId uint32, userId uint32) error {
	return uc.CommonUsecase.Bookmark(articleId, bookmarkListId, userId)
}

func (uc *ReaderUsecaseImpl) Unbookmark(bookmarkListId uint32, articleId uint32, userId uint32) error {
	return uc.CommonUsecase.Unbookmark(articleId, bookmarkListId, userId)
}

func (uc *ReaderUsecaseImpl) Subscribe(publisherId uint32, userId uint32) error {
	return uc.CommonUsecase.Subscribe(publisherId, userId)
}

func (uc *ReaderUsecaseImpl) Unsubscribe(publisherId uint32, userId uint32) error {
	return uc.CommonUsecase.Unsubscribe(publisherId, userId)
}

func (uc *ReaderUsecaseImpl) GetRelatedArticles(articleId uint32, userId uint32, count int) (*messages.RelatedArticlesResponse, error) {
	relatedArticlesDm, err := uc.RecsysRepository.GetRelatedArticles(articleId, userId, count)
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
