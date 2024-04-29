package usecases

import (
	"log/slog"
	"notime/api/messages"
	"notime/domain"
	"notime/repository"
)

type ReaderUsecaseImpl struct {
	CommonUsecase          CommonUsecase
	RecsysRepository       repository.RecsysRepository
	BookmarkListRepository domain.BookmarkListRepository
}

func (uc *ReaderUsecaseImpl) GetArticleById(id uint32, userId uint32) (*messages.Article, error) {
	return uc.CommonUsecase.GetArticleById(id, userId)
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

func (uc *ReaderUsecaseImpl) GetRelatedArticles(articleId uint32, userId uint32, count int) ([]*messages.Article, error) {
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
	return relatedArticlesApi, nil
}
