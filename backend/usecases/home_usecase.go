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

type HomeUsecaseImpl struct {
	ArticleRepository       domain.ArticleRepository
	SubscribeListRepository domain.SubscribeListRepository
	RecsysRepository        repository.RecsysRepository
	BookmarkListRepository  domain.BookmarkListRepository
	CommonUsecase           controller.CommonUsecase
}

func NewHomeUsecase(db *store.Queries) controller.HomeUsecase {
	articleRepository := repository.NewArticleRepository(db)
	bookmarkListRepository := repository.NewBookmarkListRepository(db)
	subscribeListRepository := repository.NewSubscribeListRepository(db)
	recsysRepository := repository.NewRecsysRepository()

	return &HomeUsecaseImpl{
		ArticleRepository:       articleRepository,
		SubscribeListRepository: subscribeListRepository,
		RecsysRepository:        recsysRepository,
		BookmarkListRepository:  bookmarkListRepository,
		CommonUsecase:           NewCommonUsecase(db),
	}
}

func (uc *HomeUsecaseImpl) GetSubscribedPublishers(ctx context.Context, userId int32) ([]*messages.Publisher, error) {
	subscribeListDm, err := uc.SubscribeListRepository.GetByUserId(ctx, userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetSubscribedPublishers: %v", err)
		return nil, ErrInternal
	}

	subscribeListApi, err := fromDmSubscribedPublishersToApi(subscribeListDm)
	if err != nil {
		slog.Error("[HomeUsecase] GetSubscribedPublishers: %v", err)
		return nil, ErrInternal
	}
	return subscribeListApi, nil
}

func (uc *HomeUsecaseImpl) GetLatestSubscribedArticles(ctx context.Context, count int, offset int, userId int32) ([]*messages.ArticleMetadata, error) {
	articleListDm, err := uc.RecsysRepository.GetLatestArticlesFromSubscribed(ctx, userId, count, offset)
	if err != nil {
		slog.Error("[HomeUsecase] GetLatestSubscribedArticles: %v", err)
		return nil, ErrInternal
	}

	articleListApi, err := fromDmArticlesToApi(ctx, articleListDm, userId, uc.BookmarkListRepository)
	if err != nil {
		slog.Error("[HomeUsecase] GetLatestSubscribedArticles: %v", err)
		return nil, ErrInternal
	}
	return articleListApi, nil
}

func (uc *HomeUsecaseImpl) GetLatestSubscribedArticlesByPublisher(ctx context.Context, countEachPublisher int, offset int, userId int32) ([]*messages.ArticleMetadata, error) {
	publishers, err := uc.SubscribeListRepository.GetByUserId(ctx, userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetLatestSubscribedArticlesByPublisher: %v", err)
		return nil, ErrInternal
	}

	articlesApi := make([]*messages.ArticleMetadata, 0)
	for _, publisher := range publishers {
		thisArticlesDm, err := uc.RecsysRepository.GetLatestArticlesByPublisher(ctx, publisher.Id, userId, countEachPublisher, offset)
		if err != nil {
			slog.Error("[HomeUsecase] GetLatestSubscribedArticlesByPublisher: %v", err)
			return nil, ErrInternal
		}
		thisArticlesApi, err := fromDmArticlesToApi(ctx, thisArticlesDm, userId, uc.BookmarkListRepository)
		if err != nil {
			slog.Error("[HomeUsecase] GetLatestSubscribedArticlesByPublisher: %v", err)
			return nil, ErrInternal
		}
		articlesApi = append(articlesApi, thisArticlesApi...)
	}
	return articlesApi, nil
}

func (uc *HomeUsecaseImpl) GetExploreArticles(ctx context.Context, count int, offset int, userId int32) ([]*messages.ArticleMetadata, error) {
	articleListDm, err := uc.RecsysRepository.GetLatestArticlesFromUnsubscribed(ctx, userId, count, offset)
	if err != nil {
		slog.Error("[HomeUsecase] GetExploreArticles: %v", err)
		return nil, ErrInternal
	}

	articleListApi, err := fromDmArticlesToApi(ctx, articleListDm, userId, uc.BookmarkListRepository)
	if err != nil {
		slog.Error("[HomeUsecase] GetExploreArticles: %v", err)
		return nil, ErrInternal
	}
	return articleListApi, nil

}

func (uc *HomeUsecaseImpl) Search(ctx context.Context, query string, count int, offset int, userId int32) ([]*messages.ArticleMetadata, error) {
	articleListDm, err := uc.ArticleRepository.Search(ctx, query, count, offset)
	if err != nil {
		slog.Error("[HomeUsecase] Search: %v", err)
		return nil, ErrInternal
	}

	articleListApi, err := fromDmArticlesToApi(ctx, articleListDm, userId, uc.BookmarkListRepository)
	if err != nil {
		slog.Error("[HomeUsecase] Search: %v", err)
		return nil, ErrInternal
	}
	return articleListApi, nil
}
