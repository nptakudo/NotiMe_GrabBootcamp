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
			ArticleRepository: &repository.ArticleRepositoryImpl{},
		},
	}
	group.GET("/subscribed_articles_by_date", homeController.GetSubscribedArticlesByDate)
	group.GET("/subscribed_articles_by_publisher", homeController.GetSubscribedArticlesByPublisher)
	group.GET("/explore_articles", homeController.GetExploreArticles)
}
