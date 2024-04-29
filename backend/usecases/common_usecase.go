package usecases

import (
	"log/slog"
	"notime/api/messages"
	"notime/domain"
)

type CommonUsecase interface {
	GetArticleById(id uint32, userId uint32) (*messages.Article, error)
	GetPublisherById(id uint32, userId uint32) (*messages.Publisher, error)

	IsBookmarked(articleId uint32, bookmarkListId uint32, userId uint32) (bool, error)
	IsSubscribed(publisherId uint32, userId uint32) (bool, error)

	Bookmark(articleId uint32, bookmarkListId uint32, userId uint32) error
	Unbookmark(articleId uint32, bookmarkListId uint32, userId uint32) error
	Subscribe(publisherId uint32, userId uint32) error
	Unsubscribe(publisherId uint32, userId uint32) error
}

type CommonUsecaseImpl struct {
	ArticleRepository       domain.ArticleRepository
	PublisherRepository     domain.PublisherRepository
	BookmarkListRepository  domain.BookmarkListRepository
	SubscribeListRepository domain.SubscribeListRepository
}

func (uc *CommonUsecaseImpl) GetArticleById(id uint32, userId uint32) (*messages.Article, error) {
	articleDm, err := uc.ArticleRepository.GetById(id)
	if err != nil {
		slog.Error("[HomeUsecase] GetArticleById: %v", err)
		return nil, ErrInternal
	}
	isBookmarked, err := uc.BookmarkListRepository.IsBookmarked(id, userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetArticleById: %v", err)
		return nil, ErrInternal
	}
	return FromDmArticleToApi(articleDm, isBookmarked), nil
}

func (uc *CommonUsecaseImpl) GetPublisherById(id uint32, userId uint32) (*messages.Publisher, error) {
	publisherDm, err := uc.PublisherRepository.GetById(id)
	if err != nil {
		slog.Error("[HomeUsecase] GetPublisherById: %v", err)
		return nil, ErrInternal
	}
	isSubscribed, err := uc.SubscribeListRepository.IsSubscribed(id, userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetPublisherById: %v", err)
		return nil, ErrInternal
	}
	return FromDmPublisherToApi(publisherDm, isSubscribed), nil
}

func (uc *CommonUsecaseImpl) IsBookmarked(articleId uint32, bookmarkListId uint32, userId uint32) (bool, error) {
	isBookmarked, err := uc.BookmarkListRepository.IsBookmarked(articleId, userId)
	if err != nil {
		slog.Error("[HomeUsecase] IsBookmarked: %v", err)
		return false, ErrInternal
	}
	return isBookmarked, nil
}

func (uc *CommonUsecaseImpl) IsSubscribed(publisherId uint32, userId uint32) (bool, error) {
	isSubscribed, err := uc.SubscribeListRepository.IsSubscribed(publisherId, userId)
	if err != nil {
		slog.Error("[HomeUsecase] IsSubscribed: %v", err)
		return false, ErrInternal
	}
	return isSubscribed, nil
}

func (uc *CommonUsecaseImpl) Bookmark(articleId uint32, bookmarkListId uint32, userId uint32) error {
	bookmarkListDm, err := uc.BookmarkListRepository.GetById(bookmarkListId, userId)
	if err != nil {
		slog.Error("[HomeUsecase] Bookmark: %v", err)
		return ErrInternal
	}
	if bookmarkListDm.Privilege != domain.ReadWrite {
		return ErrNotAuthorized
	}

	err = uc.BookmarkListRepository.AddToBookmarkList(bookmarkListId, articleId)
	if err != nil {
		slog.Error("[HomeUsecase] Bookmark: %v", err)
		return ErrInternal
	}
	return nil
}

func (uc *CommonUsecaseImpl) Unbookmark(articleId uint32, bookmarkListId uint32, userId uint32) error {
	bookmarkListDm, err := uc.BookmarkListRepository.GetById(bookmarkListId, userId)
	if err != nil {
		slog.Error("[HomeUsecase] Unbookmark: %v", err)
		return ErrInternal
	}
	if bookmarkListDm.Privilege != domain.ReadWrite {
		return ErrNotAuthorized
	}

	err = uc.BookmarkListRepository.RemoveFromBookmarkList(bookmarkListId, articleId)
	if err != nil {
		slog.Error("[HomeUsecase] Unbookmark: %v", err)
		return ErrInternal
	}
	return nil
}

func (uc *CommonUsecaseImpl) Subscribe(publisherId uint32, userId uint32) error {
	err := uc.SubscribeListRepository.AddToSubscribeList(publisherId, userId)
	if err != nil {
		slog.Error("[HomeUsecase] Subscribe: %v", err)
		return ErrInternal
	}
	return nil
}
func (uc *CommonUsecaseImpl) Unsubscribe(publisherId uint32, userId uint32) error {
	err := uc.SubscribeListRepository.RemoveFromSubscribeList(publisherId, userId)
	if err != nil {
		slog.Error("[HomeUsecase] Unsubscribe: %v", err)
		return ErrInternal
	}
	return nil
}
