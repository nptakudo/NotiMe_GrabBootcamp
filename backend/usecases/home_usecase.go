package usecases

import (
	"fmt"
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
}

func (uc *HomeUsecaseImpl) GetSubscribedPublishers(userId uint32) ([]*messages.Publisher, error) {
	subscribeListDm, err := uc.SubscribeListRepository.GetByUser(userId)
	if err != nil {
		slog.Error(fmt.Sprintf("[HomeUsecase] GetSubscribedPublishers: %v", err))
		return nil, ErrInternal
	}

	subscribeListApi, err := uc.fromDmSubscribedPublishersToApiPublishers(subscribeListDm)
	if err != nil {
		slog.Error(fmt.Sprintf("[HomeUsecase] GetSubscribedPublishers: %v", err))
		return nil, ErrInternal
	}
	return subscribeListApi, nil
}

func (uc *HomeUsecaseImpl) GetLatestSubscribedArticles(count int, userId uint32) ([]*messages.Article, error) {
	articleListDm, err := uc.RecsysRepository.GetLatestArticlesFromSubscribed(userId, count)
	if err != nil {
		slog.Error(fmt.Sprintf("[HomeUsecase] GetLatestSubscribedArticles: %v", err))
		return nil, ErrInternal
	}

	articleListApi, err := uc.fromDmArticlesToApiArticles(articleListDm, userId)
	if err != nil {
		slog.Error(fmt.Sprintf("[HomeUsecase] GetLatestSubscribedArticles: %v", err))
		return nil, ErrInternal
	}
	return articleListApi, nil
}

func (uc *HomeUsecaseImpl) GetLatestSubscribedArticlesByPublisher(countEachPublisher int, userId uint32) ([]*messages.Article, error) {
	publishers, err := uc.SubscribeListRepository.GetByUser(userId)
	if err != nil {
		slog.Error(fmt.Sprintf("[HomeUsecase] GetLatestSubscribedArticlesByPublisher: %v", err))
		return nil, ErrInternal
	}

	articlesApi := make([]*messages.Article, 0)
	for _, publisher := range publishers {
		thisArticlesDm, err := uc.RecsysRepository.GetLatestArticlesByPublisher(publisher.ID, userId, countEachPublisher)
		if err != nil {
			slog.Error(fmt.Sprintf("[HomeUsecase] GetLatestSubscribedArticlesByPublisher: %v", err))
			return nil, ErrInternal
		}
		thisArticlesApi, err := uc.fromDmArticlesToApiArticles(thisArticlesDm, userId)
		if err != nil {
			slog.Error(fmt.Sprintf("[HomeUsecase] GetLatestSubscribedArticlesByPublisher: %v", err))
			return nil, ErrInternal
		}
		articlesApi = append(articlesApi, thisArticlesApi...)
	}
	return articlesApi, nil
}

func (uc *HomeUsecaseImpl) GetExploreArticles(count int, userId uint32) ([]*messages.Article, error) {
	articleListDm, err := uc.RecsysRepository.GetLatestArticlesFromUnsubscribed(userId, count)
	if err != nil {
		slog.Error(fmt.Sprintf("[HomeUsecase] GetExploreArticles: %v", err))
		return nil, ErrInternal
	}

	articleListApi, err := uc.fromDmArticlesToApiArticles(articleListDm, userId)
	if err != nil {
		slog.Error(fmt.Sprintf("[HomeUsecase] GetExploreArticles: %v", err))
		return nil, ErrInternal
	}
	return articleListApi, nil

}

func (uc *HomeUsecaseImpl) Search(query string, count int, userId uint32) ([]*messages.Article, error) {
	articleListDm, err := uc.ArticleRepository.Search(query, count)
	if err != nil {
		slog.Error(fmt.Sprintf("[HomeUsecase] Search: %v", err))
		return nil, ErrInternal
	}

	articleListApi, err := uc.fromDmArticlesToApiArticles(articleListDm, userId)
	if err != nil {
		slog.Error(fmt.Sprintf("[HomeUsecase] Search: %v", err))
		return nil, ErrInternal
	}
	return articleListApi, nil
}

func (uc *HomeUsecaseImpl) Bookmark(articleId uint32, bookmarkListId uint32, userId uint32) error {
	bookmarkListDm, err := uc.BookmarkListRepository.GetByID(bookmarkListId, userId)
	if err != nil {
		slog.Error(fmt.Sprintf("[HomeUsecase] Bookmark: %v", err))
		return ErrInternal
	}
	if bookmarkListDm.Privilege != domain.ReadWrite {
		return ErrNotAuthorized
	}

	err = uc.BookmarkListRepository.AddToBookmarkList(bookmarkListId, articleId)
	if err != nil {
		slog.Error(fmt.Sprintf("[HomeUsecase] Bookmark: %v", err))
		return ErrInternal
	}
	return nil
}

func (uc *HomeUsecaseImpl) Unbookmark(articleId uint32, bookmarkListId uint32, userId uint32) error {
	bookmarkListDm, err := uc.BookmarkListRepository.GetByID(bookmarkListId, userId)
	if err != nil {
		slog.Error(fmt.Sprintf("[HomeUsecase] Unbookmark: %v", err))
		return ErrInternal
	}
	if bookmarkListDm.Privilege != domain.ReadWrite {
		return ErrNotAuthorized
	}

	err = uc.BookmarkListRepository.RemoveFromBookmarkList(bookmarkListId, articleId)
	if err != nil {
		slog.Error(fmt.Sprintf("[HomeUsecase] Unbookmark: %v", err))
		return ErrInternal
	}
	return nil
}

func (uc *HomeUsecaseImpl) Subscribe(publisherId uint32, userId uint32) error {
	err := uc.SubscribeListRepository.AddToSubscribeList(publisherId, userId)
	if err != nil {
		slog.Error(fmt.Sprintf("[HomeUsecase] Subscribe: %v", err))
		return ErrInternal
	}
	return nil
}
func (uc *HomeUsecaseImpl) Unsubscribe(publisherId uint32, userId uint32) error {
	err := uc.SubscribeListRepository.RemoveFromSubscribeList(publisherId, userId)
	if err != nil {
		slog.Error(fmt.Sprintf("[HomeUsecase] Unsubscribe: %v", err))
		return ErrInternal
	}
	return nil
}

func (uc *HomeUsecaseImpl) fromDmArticlesToApiArticles(articles []*domain.Article, userId uint32) ([]*messages.Article, error) {
	articlesApi := make([]*messages.Article, 0)
	for _, article := range articles {
		isBookmarked, err := uc.BookmarkListRepository.IsBookmarked(article.ID, userId)
		if err != nil {
			return nil, err
		}
		articlesApi = append(articlesApi, messages.FromDmToApiArticle(article, isBookmarked))
	}
	return articlesApi, nil
}

func (uc *HomeUsecaseImpl) fromDmSubscribedPublishersToApiPublishers(publishers []*domain.Publisher) ([]*messages.Publisher, error) {
	publishersApi := make([]*messages.Publisher, 0)
	for _, publisher := range publishers {
		publishersApi = append(publishersApi, messages.FromDmToApiPublisher(publisher, true))
	}
	return publishersApi, nil
}
