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
	"strings"
)

type CommonUsecaseImpl struct {
	ArticleRepository       domain.ArticleRepository
	PublisherRepository     domain.PublisherRepository
	BookmarkListRepository  domain.BookmarkListRepository
	SubscribeListRepository domain.SubscribeListRepository
}

func NewCommonUsecase(env *bootstrap.Env, db *store.Queries) controller.CommonUsecase {
	articleRepository := repository.NewArticleRepository(env, db)
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
		slog.Error("[HomeUsecase] GetArticleMetadataById:", "error", err)
		return nil, ErrInternal
	}
	isBookmarked, err := uc.BookmarkListRepository.IsInAnyBookmarkList(ctx, id, userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetArticleMetadataById:", "error", err)
		return nil, ErrInternal
	}
	return FromDmArticleToApi(articleDm, isBookmarked), nil
}

func (uc *CommonUsecaseImpl) GetPublisherById(ctx context.Context, id int32, userId int32) (*messages.Publisher, error) {
	publisherDm, err := uc.PublisherRepository.GetById(ctx, id)
	if err != nil {
		slog.Error("[HomeUsecase] GetPublisherById:", "error", err)
		return nil, ErrInternal
	}
	isSubscribed, err := uc.SubscribeListRepository.IsSubscribed(ctx, id, userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetPublisherById:", "error", err)
		return nil, ErrInternal
	}
	return FromDmPublisherToApi(publisherDm, isSubscribed), nil
}

func (uc *CommonUsecaseImpl) GetBookmarkLists(ctx context.Context, userId int32, isShared bool) ([]*messages.BookmarkList, error) {
	bookmarkListsDm, err := uc.BookmarkListRepository.GetOwnByUser(ctx, userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetBookmarkLists:", "error", err)
		return nil, ErrInternal
	}

	if isShared {
		shareBookmarkListsDm, err := uc.BookmarkListRepository.GetSharedWithUser(ctx, userId)
		if err != nil {
			slog.Error("[HomeUsecase] GetBookmarkLists:", "error", err)
			return nil, ErrInternal
		}
		bookmarkListsDm = append(bookmarkListsDm, shareBookmarkListsDm...)
	}

	return fromDmBookmarkListsToApi(bookmarkListsDm), nil
}

func (uc *CommonUsecaseImpl) GetBookmarkListById(ctx context.Context, id int32, userId int32) (*messages.BookmarkList, error) {
	bookmarkListDm, err := uc.BookmarkListRepository.GetById(ctx, id)
	if err != nil {
		slog.Error("[HomeUsecase] GetBookmarkListById:", "error", err)
		return nil, ErrInternal
	}
	return fromDmBookmarkListToApi(bookmarkListDm), nil
}

func (uc *CommonUsecaseImpl) GetSubscriptions(ctx context.Context, userId int32) ([]*messages.Publisher, error) {
	subscribedPublishersDm, err := uc.SubscribeListRepository.GetByUserId(ctx, userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetSubscriptions:", "error", err)
		return nil, ErrInternal
	}
	return fromDmSubscribedPublishersToApi(subscribedPublishersDm)
}

func (uc *CommonUsecaseImpl) IsBookmarked(ctx context.Context, articleId int64, bookmarkListId int32) (bool, error) {
	isBookmarked, err := uc.BookmarkListRepository.IsInBookmarkList(ctx, articleId, bookmarkListId)
	if err != nil {
		slog.Error("[HomeUsecase] IsInBookmarkList:", "error", err)
		return false, ErrInternal
	}
	return isBookmarked, nil
}

func (uc *CommonUsecaseImpl) IsSubscribed(ctx context.Context, publisherId int32, userId int32) (bool, error) {
	isSubscribed, err := uc.SubscribeListRepository.IsSubscribed(ctx, publisherId, userId)
	if err != nil {
		slog.Error("[HomeUsecase] IsSubscribed:", "error", err)
		return false, ErrInternal
	}
	return isSubscribed, nil
}

func (uc *CommonUsecaseImpl) Bookmark(ctx context.Context, articleId int64, bookmarkListId int32, userId int32) error {
	bookmarkListDm, err := uc.BookmarkListRepository.GetById(ctx, bookmarkListId)
	if err != nil {
		slog.Error("[HomeUsecase] Bookmark:", "error", err)
		return ErrInternal
	}
	if bookmarkListDm.OwnerId != userId {
		return ErrNotAuthorized
	}

	err = uc.BookmarkListRepository.AddToBookmarkList(ctx, articleId, bookmarkListId)
	if err != nil {
		slog.Error("[HomeUsecase] Bookmark:", "error", err)
		return ErrInternal
	}
	return nil
}

func (uc *CommonUsecaseImpl) Unbookmark(ctx context.Context, articleId int64, bookmarkListId int32, userId int32) error {
	bookmarkListDm, err := uc.BookmarkListRepository.GetById(ctx, bookmarkListId)
	if err != nil {
		slog.Error("[HomeUsecase] Unbookmark:", "error", err)
		return ErrInternal
	}
	if bookmarkListDm.OwnerId != userId {
		return ErrNotAuthorized
	}

	err = uc.BookmarkListRepository.RemoveFromBookmarkList(ctx, articleId, bookmarkListId)
	if err != nil {
		slog.Error("[HomeUsecase] Unbookmark:", "error", err)
		return ErrInternal
	}
	return nil
}

func (uc *CommonUsecaseImpl) Subscribe(ctx context.Context, publisherId int32, userId int32) error {
	err := uc.SubscribeListRepository.AddToSubscribeList(ctx, publisherId, userId)
	if err != nil {
		slog.Error("[HomeUsecase] Subscribe:", "error", err)
		return ErrInternal
	}
	return nil
}
func (uc *CommonUsecaseImpl) Unsubscribe(ctx context.Context, publisherId int32, userId int32) error {
	err := uc.SubscribeListRepository.RemoveFromSubscribeList(ctx, publisherId, userId)
	if err != nil {
		slog.Error("[HomeUsecase] Unsubscribe:", "error", err)
		return ErrInternal
	}
	return nil
}
func (uc *CommonUsecaseImpl) SearchPublisher(ctx context.Context, searchQuery string, userId int) ([]*messages.Publisher, error) {
	publishersDm := make([]*domain.Publisher, 0)

	if strings.HasPrefix(searchQuery, "https://") {
		if !strings.HasSuffix(searchQuery, "/") {
			searchQuery += "/"
		}
		publisherDm, err := uc.PublisherRepository.SearchByUrl(ctx, searchQuery)
		if err != nil {
			return nil, ErrInternal
		}

		if publisherDm == nil {
			return nil, nil
		}

		publishersDm = append(publishersDm, publisherDm)
	} else {
		publishers, err := uc.PublisherRepository.SearchByName(ctx, searchQuery)
		if err != nil {
			slog.Error("[HomeUsecase] SearchPublisher:", "error", err)
			return nil, ErrInternal
		}
		publishersDm = append(publishersDm, publishers...)
	}

	results := make([]*messages.Publisher, 0)
	for _, publisher := range publishersDm {
		isSubscribed, err := uc.IsSubscribed(ctx, publisher.Id, int32(userId))
		if err != nil {
			slog.Error("[CommonUsecase] SearchPublisher - IsUserSubscribed:", "error", err)
			return nil, ErrInternal
		}
		results = append(results, FromDmPublisherToApi(publisher, isSubscribed))
	}

	return results, nil
}

func (uc *CommonUsecaseImpl) GetArticlesByPublisher(ctx context.Context, publisherId int32, userId int32, count int, offset int) ([]*messages.ArticleMetadata, error) {
	articlesDm, err := uc.ArticleRepository.GetByPublisher(ctx, publisherId, count, offset)
	if err != nil {
		slog.Error("[HomeUsecase] GetArticlesByPublisher:", "error", err)
		return nil, ErrInternal
	}
	return fromDmArticlesToApi(ctx, articlesDm, userId, uc.BookmarkListRepository)
}

func (uc *CommonUsecaseImpl) AddNewSource(ctx context.Context, source domain.Publisher) (int, error) {
	newPublisher, err := uc.PublisherRepository.Create(ctx, source.Name, source.Url, source.AvatarUrl)
	if err != nil {
		slog.Error("[HomeUsecase] AddNewSource:", "error", err)
		return 0, ErrInternal
	}
	return int(newPublisher.Id), nil
}
func (uc *CommonUsecaseImpl) AddNewArticle(ctx context.Context, article domain.ArticleMetadata) error {
	_, err := uc.ArticleRepository.Create(ctx, article.Title, article.Date, article.Url, article.Publisher.Id)
	if err != nil {
		slog.Error("[HomeUsecase] AddNewArticle:", "error", err)
		return ErrInternal
	}
	return nil
}
