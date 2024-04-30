package route

import (
	"github.com/gin-gonic/gin"
	"notime/api/controller"
	"notime/repository"
	"notime/usecases"
)

func CommonRouter(group *gin.RouterGroup) {
	commonController := controller.CommonController{
		CommonUsecase: &usecases.CommonUsecaseImpl{
			ArticleRepository:       &repository.ArticleRepositoryImpl{},
			PublisherRepository:     &repository.PublisherRepositoryImpl{},
			BookmarkListRepository:  &repository.BookmarkListRepositoryImpl{},
			SubscribeListRepository: &repository.SubscribeListRepositoryImpl{},
		},
	}
	// Add article to bookmark list
	group.GET("/:article_id/bookmark/:bookmark_id", commonController.Bookmark)
	// Remove article from bookmark list
	group.GET("/:article_id/unbookmark/:bookmark_id", commonController.Unbookmark)
	// Subscribe to publisher
	group.GET("/:publisher_id/subscribe", commonController.Subscribe)
	// Unsubscribe from publisher
	group.GET("/:publisher_id/unsubscribe", commonController.Unsubscribe)
}
