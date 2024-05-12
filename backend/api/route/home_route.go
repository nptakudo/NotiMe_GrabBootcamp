package route

import (
	"github.com/gin-gonic/gin"
	"notime/api/controller"
	"notime/bootstrap"
	"notime/external/sql/store"
	"notime/usecases"
)

func NewHomeRouter(group *gin.RouterGroup, env *bootstrap.Env, db *store.Queries) {
	homeController := controller.HomeController{
		HomeUsecase: usecases.NewHomeUsecase(env, db),
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
