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
)

type HomeUsecaseImpl struct {
	ArticleRepository       domain.ArticleRepository
	SubscribeListRepository domain.SubscribeListRepository
	RecsysRepository        repository.RecsysRepository
	BookmarkListRepository  domain.BookmarkListRepository
	CommonUsecase           controller.CommonUsecase
}

func NewHomeUsecase(env *bootstrap.Env, db *store.Queries) controller.HomeUsecase {
	articleRepository := repository.NewArticleRepository(env, db)
	bookmarkListRepository := repository.NewBookmarkListRepository(env, db)
	subscribeListRepository := repository.NewSubscribeListRepository(db)
	recsysRepository := repository.NewRecsysRepository(env, db)

	return &HomeUsecaseImpl{
		ArticleRepository:       articleRepository,
		SubscribeListRepository: subscribeListRepository,
		RecsysRepository:        recsysRepository,
		BookmarkListRepository:  bookmarkListRepository,
		CommonUsecase:           NewCommonUsecase(env, db),
	}
}

func (uc *HomeUsecaseImpl) GetSubscribedPublishers(ctx context.Context, userId int32) ([]*messages.Publisher, error) {
	subscribeListDm, err := uc.SubscribeListRepository.GetByUserId(ctx, userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetSubscribedPublishers:", "error", err)
		return nil, ErrInternal
	}

	subscribeListApi, err := fromDmSubscribedPublishersToApi(subscribeListDm)
	if err != nil {
		slog.Error("[HomeUsecase] GetSubscribedPublishers:", "error", err)
		return nil, ErrInternal
	}
	return subscribeListApi, nil
}

func (uc *HomeUsecaseImpl) GetLatestSubscribedArticles(ctx context.Context, count int, offset int, userId int32) ([]*messages.ArticleMetadata, error) {
	articleListDm, err := uc.RecsysRepository.GetLatestArticlesFromSubscribed(ctx, userId, count, offset)
	if err != nil {
		slog.Error("[HomeUsecase] GetLatestSubscribedArticles:", "error", err)
		return nil, ErrInternal
	}

	articleListApi, err := fromDmArticlesToApi(ctx, articleListDm, userId, uc.BookmarkListRepository)
	if err != nil {
		slog.Error("[HomeUsecase] GetLatestSubscribedArticles:", "error", err)
		return nil, ErrInternal
	}
	return articleListApi, nil
}

func (uc *HomeUsecaseImpl) GetLatestSubscribedArticlesByPublisher(ctx context.Context, countEachPublisher int, offset int, userId int32) ([]*messages.ArticleMetadata, error) {
	publishers, err := uc.SubscribeListRepository.GetByUserId(ctx, userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetLatestSubscribedArticlesByPublisher:", "error", err)
		return nil, ErrInternal
	}

	articlesApi := make([]*messages.ArticleMetadata, 0)
	for _, publisher := range publishers {
		thisArticlesDm, err := uc.RecsysRepository.GetLatestArticlesByPublisher(ctx, publisher.Id, userId, countEachPublisher, offset)
		if err != nil {
			slog.Error("[HomeUsecase] GetLatestSubscribedArticlesByPublisher:", "error", err)
			return nil, ErrInternal
		}
		thisArticlesApi, err := fromDmArticlesToApi(ctx, thisArticlesDm, userId, uc.BookmarkListRepository)
		if err != nil {
			slog.Error("[HomeUsecase] GetLatestSubscribedArticlesByPublisher:", "error", err)
			return nil, ErrInternal
		}
		articlesApi = append(articlesApi, thisArticlesApi...)
	}
	return articlesApi, nil
}

func (uc *HomeUsecaseImpl) GetExploreArticles(ctx context.Context, count int, offset int, userId int32) ([]*messages.ArticleMetadata, error) {
	articleListDm, err := uc.RecsysRepository.GetLatestArticlesFromUnsubscribed(ctx, userId, count, offset)
	if err != nil {
		slog.Error("[HomeUsecase] GetExploreArticles:", "error", err)
		return nil, ErrInternal
	}

	articleListApi, err := fromDmArticlesToApi(ctx, articleListDm, userId, uc.BookmarkListRepository)
	if err != nil {
		slog.Error("[HomeUsecase] GetExploreArticles:", "error", err)
		return nil, ErrInternal
	}
	return articleListApi, nil

}

func (uc *HomeUsecaseImpl) Search(ctx context.Context, query string, count int, offset int, userId int32) ([]*messages.ArticleMetadata, error) {
	articleListDm, err := uc.ArticleRepository.Search(ctx, query, count, offset)
	if err != nil {
		slog.Error("[HomeUsecase] Search:", "error", err)
		return nil, ErrInternal
	}

	articleListApi, err := fromDmArticlesToApi(ctx, articleListDm, userId, uc.BookmarkListRepository)
	if err != nil {
		slog.Error("[HomeUsecase] Search:", "error", err)
		return nil, ErrInternal
	}
	return articleListApi, nil
}
