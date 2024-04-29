package usecases

import (
	"notime/api/messages"
	"notime/domain"
)

func fromApiArticleToDm(a *messages.Article) *domain.Article {
	return &a.Article
}

func FromDmArticleToApi(a *domain.Article, isBookmarked bool) *messages.Article {
	return &messages.Article{Article: *a, IsBookmarked: isBookmarked}
}

func FromApiPublisherToDm(p *messages.Publisher) *domain.Publisher {
	return &p.Publisher
}

func FromDmPublisherToApi(p *domain.Publisher, isSubscribed bool) *messages.Publisher {
	return &messages.Publisher{Publisher: *p, IsSubscribed: isSubscribed}
}

func fromDmArticlesToApi(articles []*domain.Article, userId uint32, bookmarkListRepository domain.BookmarkListRepository) ([]*messages.Article, error) {
	articlesApi := make([]*messages.Article, 0)
	for _, article := range articles {
		isBookmarked, err := bookmarkListRepository.IsBookmarked(article.Id, userId)
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
