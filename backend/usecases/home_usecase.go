package usecases

import (
	"log/slog"
	"notime/api/controller"
	"notime/api/messages"
	"notime/domain"
	"notime/repository"
)

type HomeUsecaseImpl struct {
	ArticleRepository       domain.ArticleRepository
	SubscribeListRepository domain.SubscribeListRepository
	RecsysRepository        repository.RecsysRepository
	BookmarkListRepository  domain.BookmarkListRepository
	CommonUsecase           controller.CommonUsecase
}

func (uc *HomeUsecaseImpl) GetSubscribedPublishers(userId uint32) ([]*messages.Publisher, error) {
	subscribeListDm, err := uc.SubscribeListRepository.GetByUserId(userId)
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

func (uc *HomeUsecaseImpl) GetLatestSubscribedArticles(count int, offset int, userId uint32) ([]*messages.ArticleMetadata, error) {
	articleListDm, err := uc.RecsysRepository.GetLatestArticlesFromSubscribed(userId, count, offset)
	if err != nil {
		slog.Error("[HomeUsecase] GetLatestSubscribedArticles: %v", err)
		return nil, ErrInternal
	}

	articleListApi, err := fromDmArticlesToApi(articleListDm, userId, uc.BookmarkListRepository)
	if err != nil {
		slog.Error("[HomeUsecase] GetLatestSubscribedArticles: %v", err)
		return nil, ErrInternal
	}
	return articleListApi, nil
}

func (uc *HomeUsecaseImpl) GetLatestSubscribedArticlesByPublisher(countEachPublisher int, offset int, userId uint32) ([]*messages.ArticleMetadata, error) {
	publishers, err := uc.SubscribeListRepository.GetByUserId(userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetLatestSubscribedArticlesByPublisher: %v", err)
		return nil, ErrInternal
	}

	articlesApi := make([]*messages.ArticleMetadata, 0)
	for _, publisher := range publishers {
		thisArticlesDm, err := uc.RecsysRepository.GetLatestArticlesByPublisher(publisher.Id, userId, countEachPublisher, offset)
		if err != nil {
			slog.Error("[HomeUsecase] GetLatestSubscribedArticlesByPublisher: %v", err)
			return nil, ErrInternal
		}
		thisArticlesApi, err := fromDmArticlesToApi(thisArticlesDm, userId, uc.BookmarkListRepository)
		if err != nil {
			slog.Error("[HomeUsecase] GetLatestSubscribedArticlesByPublisher: %v", err)
			return nil, ErrInternal
		}
		articlesApi = append(articlesApi, thisArticlesApi...)
	}
	return articlesApi, nil
}

func (uc *HomeUsecaseImpl) GetExploreArticles(count int, offset int, userId uint32) ([]*messages.ArticleMetadata, error) {
	articleListDm, err := uc.RecsysRepository.GetLatestArticlesFromUnsubscribed(userId, count, offset)
	if err != nil {
		slog.Error("[HomeUsecase] GetExploreArticles: %v", err)
		return nil, ErrInternal
	}

	articleListApi, err := fromDmArticlesToApi(articleListDm, userId, uc.BookmarkListRepository)
	if err != nil {
		slog.Error("[HomeUsecase] GetExploreArticles: %v", err)
		return nil, ErrInternal
	}
	return articleListApi, nil

}

func (uc *HomeUsecaseImpl) Search(query string, count int, offset int, userId uint32) ([]*messages.ArticleMetadata, error) {
	articleListDm, err := uc.ArticleRepository.Search(query, count, offset)
	if err != nil {
		slog.Error("[HomeUsecase] Search: %v", err)
		return nil, ErrInternal
	}

	articleListApi, err := fromDmArticlesToApi(articleListDm, userId, uc.BookmarkListRepository)
	if err != nil {
		slog.Error("[HomeUsecase] Search: %v", err)
		return nil, ErrInternal
	}
	return articleListApi, nil
}
