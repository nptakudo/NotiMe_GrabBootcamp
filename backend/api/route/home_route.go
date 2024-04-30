package route

import (
	"github.com/gin-gonic/gin"
	"notime/api/controller"
	"notime/repository"
	"notime/usecases"
)

func NewHomeRouter(group *gin.RouterGroup) {
	homeController := controller.HomeController{
		HomeUsecase: &usecases.HomeUsecaseImpl{
			ArticleRepository:       &repository.ArticleRepositoryImpl{},
			SubscribeListRepository: &repository.SubscribeListRepositoryImpl{},
			RecsysRepository:        &repository.RecsysRepositoryImpl{},
			BookmarkListRepository:  &repository.BookmarkListRepositoryImpl{},
			CommonUsecase:           &usecases.CommonUsecaseImpl{},
		},
	}
	// Get latest articles from subscribed publishers
	// Query params: count, offset
	group.GET("/latest_subscribed_articles", homeController.GetLatestSubscribedArticles)
	// Get latest articles from subscribed publishers by publisher
	// Query params: count, offset
	group.GET("/latest_subscribed_articles_by_publisher", homeController.GetLatestSubscribedArticlesByPublisher)
	// Get latest articles from unsubscribed publishers
	// Query params: count, offset
	group.GET("/explore_articles", homeController.GetExploreArticles)
}
