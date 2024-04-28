package usecases

import "notime/domain"

// HomeUsecaseImpl TODO
type HomeUsecaseImpl struct {
	ArticleRepository domain.ArticleRepository
}

func (usecase *HomeUsecaseImpl) GetSubscribedArticlesByDate(count int, userId uint32) ([]*domain.Article, error) {
	return nil, nil
}
func (usecase *HomeUsecaseImpl) GetSubscribedArticlesByPublisher(countEachPublisher int, userId uint32) ([]*domain.Article, error) {
	return nil, nil
}
func (usecase *HomeUsecaseImpl) GetExploreArticles(count int, userId uint32) ([]*domain.Article, error) {
	return nil, nil
}
func (usecase *HomeUsecaseImpl) Search(query string, count int, userId uint32) ([]*domain.Article, error) {
	return nil, nil
}
