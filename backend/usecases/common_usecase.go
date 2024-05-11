package usecases

import (
	"context"
	"log/slog"
	"notime/api/controller"
	"notime/api/messages"
	"notime/domain"
	"notime/external/sql/store"
	"notime/repository"
)

type CommonUsecaseImpl struct {
	ArticleRepository       domain.ArticleRepository
	PublisherRepository     domain.PublisherRepository
	BookmarkListRepository  domain.BookmarkListRepository
	SubscribeListRepository domain.SubscribeListRepository
}

func NewCommonUsecase(db *store.Queries) controller.CommonUsecase {
	articleRepository := repository.NewArticleRepository(db)
	publisherRepository := repository.NewPublisherRepository(db)
	bookmarkListRepository := repository.NewBookmarkListRepository(db)
	subscribeListRepository := repository.NewSubscribeListRepository(db)

	return &CommonUsecaseImpl{
		ArticleRepository:       articleRepository,
		PublisherRepository:     publisherRepository,
		BookmarkListRepository:  bookmarkListRepository,
		SubscribeListRepository: subscribeListRepository,
	}
}

func (uc *CommonUsecaseImpl) GetArticleMetadataById(ctx context.Context, id int64, userId int32) (*messages.ArticleMetadata, error) {
	articleDm, err := uc.ArticleRepository.GetById(ctx, id)
	if err != nil {
		slog.Error("[HomeUsecase] GetArticleMetadataById:", err)
		return nil, ErrInternal
	}
	isBookmarked, err := uc.BookmarkListRepository.IsInBookmarkList(ctx, id, userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetArticleMetadataById:", err)
		return nil, ErrInternal
	}
	return FromDmArticleToApi(articleDm, isBookmarked), nil
}

func (uc *CommonUsecaseImpl) GetPublisherById(ctx context.Context, id int32, userId int32) (*messages.Publisher, error) {
	publisherDm, err := uc.PublisherRepository.GetById(ctx, id)
	if err != nil {
		slog.Error("[HomeUsecase] GetPublisherById:", err)
		return nil, ErrInternal
	}
	isSubscribed, err := uc.SubscribeListRepository.IsSubscribed(ctx, id, userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetPublisherById:", err)
		return nil, ErrInternal
	}
	return FromDmPublisherToApi(publisherDm, isSubscribed), nil
}

func (uc *CommonUsecaseImpl) GetBookmarkLists(ctx context.Context, userId int32) ([]*messages.BookmarkList, error) {
	bookmarkListsDm, err := uc.BookmarkListRepository.GetOwnByUser(ctx, userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetBookmarkLists:", err)
		return nil, ErrInternal
	}
	return fromDmBookmarkListsToApi(bookmarkListsDm), nil
}

func (uc *CommonUsecaseImpl) GetBookmarkListById(ctx context.Context, id int32, userId int32) (*messages.BookmarkList, error) {
	bookmarkListDm, err := uc.BookmarkListRepository.GetById(ctx, id)
	if err != nil {
		slog.Error("[HomeUsecase] GetBookmarkListById:", err)
		return nil, ErrInternal
	}
	return fromDmBookmarkListToApi(bookmarkListDm), nil
}

func (uc *CommonUsecaseImpl) GetSubscriptions(ctx context.Context, userId int32) ([]*messages.Publisher, error) {
	subscribedPublishersDm, err := uc.SubscribeListRepository.GetByUserId(ctx, userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetSubscriptions:", err)
		return nil, ErrInternal
	}
	return fromDmSubscribedPublishersToApi(subscribedPublishersDm)
}

func (uc *CommonUsecaseImpl) IsBookmarked(ctx context.Context, articleId int64, bookmarkListId int32) (bool, error) {
	isBookmarked, err := uc.BookmarkListRepository.IsInBookmarkList(ctx, articleId, bookmarkListId)
	if err != nil {
		slog.Error("[HomeUsecase] IsInBookmarkList:", err)
		return false, ErrInternal
	}
	return isBookmarked, nil
}

func (uc *CommonUsecaseImpl) IsSubscribed(ctx context.Context, publisherId int32, userId int32) (bool, error) {
	isSubscribed, err := uc.SubscribeListRepository.IsSubscribed(ctx, publisherId, userId)
	if err != nil {
		slog.Error("[HomeUsecase] IsSubscribed:", err)
		return false, ErrInternal
	}
	return isSubscribed, nil
}

func (uc *CommonUsecaseImpl) Bookmark(ctx context.Context, articleId int64, bookmarkListId int32, userId int32) error {
	bookmarkListDm, err := uc.BookmarkListRepository.GetById(ctx, bookmarkListId)
	if err != nil {
		slog.Error("[HomeUsecase] Bookmark:", err)
		return ErrInternal
	}
	if bookmarkListDm.OwnerId != userId {
		return ErrNotAuthorized
	}

	err = uc.BookmarkListRepository.AddToBookmarkList(ctx, articleId, bookmarkListId)
	if err != nil {
		slog.Error("[HomeUsecase] Bookmark:", err)
		return ErrInternal
	}
	return nil
}

func (uc *CommonUsecaseImpl) Unbookmark(ctx context.Context, articleId int64, bookmarkListId int32, userId int32) error {
	bookmarkListDm, err := uc.BookmarkListRepository.GetById(ctx, bookmarkListId)
	if err != nil {
		slog.Error("[HomeUsecase] Unbookmark:", err)
		return ErrInternal
	}
	if bookmarkListDm.OwnerId != userId {
		return ErrNotAuthorized
	}

	err = uc.BookmarkListRepository.RemoveFromBookmarkList(ctx, articleId, bookmarkListId)
	if err != nil {
		slog.Error("[HomeUsecase] Unbookmark:", err)
		return ErrInternal
	}
	return nil
}

func (uc *CommonUsecaseImpl) Subscribe(ctx context.Context, publisherId int32, userId int32) error {
	err := uc.SubscribeListRepository.AddToSubscribeList(ctx, publisherId, userId)
	if err != nil {
		slog.Error("[HomeUsecase] Subscribe:", err)
		return ErrInternal
	}
	return nil
}
func (uc *CommonUsecaseImpl) Unsubscribe(ctx context.Context, publisherId int32, userId int32) error {
	err := uc.SubscribeListRepository.RemoveFromSubscribeList(ctx, publisherId, userId)
	if err != nil {
		slog.Error("[HomeUsecase] Unsubscribe:", err)
		return ErrInternal
	}
	return nil
}
