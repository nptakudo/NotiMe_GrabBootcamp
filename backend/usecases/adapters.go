package usecases

import (
	"context"
	"notime/api/messages"
	"notime/domain"
)

func fromApiArticleToDm(a *messages.ArticleMetadata) *domain.ArticleMetadata {
	return &a.ArticleMetadata
}

func FromDmArticleToApi(a *domain.ArticleMetadata, isBookmarked bool) *messages.ArticleMetadata {
	return &messages.ArticleMetadata{ArticleMetadata: *a, IsBookmarked: isBookmarked}
}

func FromApiPublisherToDm(p *messages.Publisher) *domain.Publisher {
	return &p.Publisher
}

func FromDmPublisherToApi(p *domain.Publisher, isSubscribed bool) *messages.Publisher {
	return &messages.Publisher{Publisher: *p, IsSubscribed: isSubscribed}
}

func fromDmBookmarkListToApi(bookmarkList *domain.BookmarkList) *messages.BookmarkList {
	return &messages.BookmarkList{BookmarkList: *bookmarkList}
}

func fromDmArticlesToApi(ctx context.Context, articles []*domain.ArticleMetadata, userId int32, bookmarkListRepository domain.BookmarkListRepository) ([]*messages.ArticleMetadata, error) {
	articlesApi := make([]*messages.ArticleMetadata, 0)
	for _, article := range articles {
		isBookmarked, err := bookmarkListRepository.IsInAnyBookmarkList(ctx, article.Id, userId)
		if err != nil {
			return nil, err
		}
		articlesApi = append(articlesApi, FromDmArticleToApi(article, isBookmarked))
	}
	return articlesApi, nil
}

func fromDmSubscribedPublishersToApi(publishers []*domain.Publisher) ([]*messages.Publisher, error) {
	publishersApi := make([]*messages.Publisher, 0)
	for _, publisher := range publishers {
		publishersApi = append(publishersApi, FromDmPublisherToApi(publisher, true))
	}
	return publishersApi, nil
}

func fromDmBookmarkListsToApi(bookmarkLists []*domain.BookmarkList) []*messages.BookmarkList {
	bookmarkListsApi := make([]*messages.BookmarkList, 0)
	for _, bookmarkList := range bookmarkLists {
		bookmarkListsApi = append(bookmarkListsApi, fromDmBookmarkListToApi(bookmarkList))
	}
	return bookmarkListsApi
}
