package usecases

import (
	"log/slog"
	"notime/api/messages"
	"notime/domain"
	"notime/repository"
)

type HomeUsecaseImpl struct {
	ArticleRepository       domain.ArticleRepository
	SubscribeListRepository domain.SubscribeListRepository
	RecsysRepository        repository.RecsysRepository
	BookmarkListRepository  domain.BookmarkListRepository
	CommonUsecase           CommonUsecase
}

func (uc *HomeUsecaseImpl) GetSubscribedPublishers(userId uint32) ([]*messages.Publisher, error) {
	subscribeListDm, err := uc.SubscribeListRepository.GetByUser(userId)
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

func (uc *HomeUsecaseImpl) GetLatestSubscribedArticles(count int, userId uint32) ([]*messages.ArticleMetadata, error) {
	articleListDm, err := uc.RecsysRepository.GetLatestArticlesFromSubscribed(userId, count)
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

func (uc *HomeUsecaseImpl) GetLatestSubscribedArticlesByPublisher(countEachPublisher int, userId uint32) ([]*messages.ArticleMetadata, error) {
	publishers, err := uc.SubscribeListRepository.GetByUser(userId)
	if err != nil {
		slog.Error("[HomeUsecase] GetLatestSubscribedArticlesByPublisher: %v", err)
		return nil, ErrInternal
	}

	articlesApi := make([]*messages.ArticleMetadata, 0)
	for _, publisher := range publishers {
		thisArticlesDm, err := uc.RecsysRepository.GetLatestArticlesByPublisher(publisher.Id, userId, countEachPublisher)
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

func (uc *HomeUsecaseImpl) GetExploreArticles(count int, userId uint32) ([]*messages.ArticleMetadata, error) {
	articleListDm, err := uc.RecsysRepository.GetLatestArticlesFromUnsubscribed(userId, count)
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

func (uc *HomeUsecaseImpl) Search(query string, count int, userId uint32) ([]*messages.ArticleMetadata, error) {
	articleListDm, err := uc.ArticleRepository.Search(query, count)
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

func (uc *HomeUsecaseImpl) Bookmark(articleId uint32, bookmarkListId uint32, userId uint32) error {
	return uc.CommonUsecase.Bookmark(articleId, bookmarkListId, userId)
}

func (uc *HomeUsecaseImpl) Unbookmark(articleId uint32, bookmarkListId uint32, userId uint32) error {
	return uc.CommonUsecase.Unbookmark(articleId, bookmarkListId, userId)
}

func (uc *HomeUsecaseImpl) Subscribe(publisherId uint32, userId uint32) error {
	return uc.CommonUsecase.Subscribe(publisherId, userId)
}
func (uc *HomeUsecaseImpl) Unsubscribe(publisherId uint32, userId uint32) error {
	return uc.CommonUsecase.Unsubscribe(publisherId, userId)
}
