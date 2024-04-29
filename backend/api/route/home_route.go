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
	group.GET("/subscribed_articles_by_date", homeController.GetLatestSubscribedArticles)
	group.GET("/subscribed_articles_by_publisher", homeController.GetLatestSubscribedArticlesByPublisher)
	group.GET("/explore_articles", homeController.GetExploreArticles)
}
