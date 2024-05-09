package usecases

import (
	"log/slog"
	"notime/api/messages"
	"notime/domain"
)

type CommonUsecaseImpl struct {
	ArticleRepository       domain.ArticleRepository
	PublisherRepository     domain.PublisherRepository
	BookmarkListRepository  domain.BookmarkListRepository
	SubscribeListRepository domain.SubscribeListRepository
}

func (uc *CommonUsecaseImpl) GetArticleMetadataById(id uint32, userId uint32) (*messages.ArticleMetadata, error) {
	articleDm, err := uc.ArticleRepository.GetById(id)
	if err != nil {
		slog.Error("[HomeUsecase] GetArticleMetadataById: %v", err)
		return nil, ErrInternal
	}
	isBookmarked, err := uc.BookmarkListRepository.IsInBookmarkList(id, userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetArticleMetadataById: %v", err)
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

func (uc *CommonUsecaseImpl) GetBookmarkLists(userId uint32) ([]*messages.BookmarkList, error) {
	bookmarkListsDm, err := uc.BookmarkListRepository.GetOwnByUser(userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetBookmarkLists: %v", err)
		return nil, ErrInternal
	}
	return fromDmBookmarkListsToApi(bookmarkListsDm), nil
}

func (uc *CommonUsecaseImpl) GetBookmarkListById(id uint32, userId uint32) (*messages.BookmarkList, error) {
	bookmarkListDm, err := uc.BookmarkListRepository.GetById(id)
	if err != nil {
		slog.Error("[HomeUsecase] GetBookmarkListById: %v", err)
		return nil, ErrInternal
	}
	return fromDmBookmarkListToApi(bookmarkListDm), nil
}

func (uc *CommonUsecaseImpl) GetSubscriptions(userId uint32) ([]*messages.Publisher, error) {
	subscribedPublishersDm, err := uc.SubscribeListRepository.GetByUserId(userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetSubscriptions: %v", err)
		return nil, ErrInternal
	}
	return fromDmSubscribedPublishersToApi(subscribedPublishersDm)
}

func (uc *CommonUsecaseImpl) IsBookmarked(articleId uint32, bookmarkListId uint32) (bool, error) {
	isBookmarked, err := uc.BookmarkListRepository.IsInBookmarkList(articleId, bookmarkListId)
	if err != nil {
		slog.Error("[HomeUsecase] IsInBookmarkList: %v", err)
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
	bookmarkListDm, err := uc.BookmarkListRepository.GetById(bookmarkListId)
	if err != nil {
		slog.Error("[HomeUsecase] Bookmark: %v", err)
		return ErrInternal
	}
	if bookmarkListDm.OwnerId != userId {
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
	bookmarkListDm, err := uc.BookmarkListRepository.GetById(bookmarkListId)
	if err != nil {
		slog.Error("[HomeUsecase] Unbookmark: %v", err)
		return ErrInternal
	}
	if bookmarkListDm.OwnerId != userId {
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
