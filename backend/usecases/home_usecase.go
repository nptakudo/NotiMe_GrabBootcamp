package usecases

import (
	"notime/api/messages"
	"notime/domain"
)

// HomeUsecaseImpl TODO
type HomeUsecaseImpl struct {
	ArticleRepository domain.ArticleRepository
}

func (usecase *HomeUsecaseImpl) GetSubscribedPublishers(count int, userId uint32) ([]*messages.Publisher, error) {
	return nil, nil
}
func (usecase *HomeUsecaseImpl) GetSubscribedArticlesByDate(count int, userId uint32) ([]*messages.Article, error) {
	return nil, nil
}
func (usecase *HomeUsecaseImpl) GetSubscribedArticlesByPublisher(countEachPublisher int, userId uint32) ([]*messages.Article, error) {
	return nil, nil
}
func (usecase *HomeUsecaseImpl) GetExploreArticles(count int, userId uint32) ([]*messages.Article, error) {
	return nil, nil
}
func (usecase *HomeUsecaseImpl) Search(query string, count int, userId uint32) ([]*messages.Article, error) {
	return nil, nil
}
func (usecase *HomeUsecaseImpl) Bookmark(articleId uint32, bookmarkListId uint32, userId uint32) (*messages.Article, error) {
	return nil, nil
}
func (usecase *HomeUsecaseImpl) Unbookmark(articleId uint32, bookmarkListId uint32, userId uint32) (*messages.Article, error) {
	return nil, nil
}
func (usecase *HomeUsecaseImpl) Subscribe(publisherId uint32, userId uint32) (*messages.Publisher, error) {
	return nil, nil
}
func (usecase *HomeUsecaseImpl) Unsubscribe(publisherId uint32, userId uint32) (*messages.Publisher, error) {
	return nil, nil
}
