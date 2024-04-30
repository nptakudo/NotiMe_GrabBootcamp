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
	// Get publisher by id
	group.GET("/publisher/:publisher_id", commonController.GetPublisherById)

	// Get all bookmark lists
	group.GET("/bookmarks", commonController.GetBookmarkLists)
	// Get bookmark list by id
	group.GET("/bookmarks/:bookmark_id", commonController.GetBookmarkListById)
	// Get whether article is bookmarked
	group.GET("/bookmarks/:bookmark_id/:article_id", commonController.IsBookmarked)
	// Add article to bookmark list
	group.PUT("/bookmarks/:bookmark_id/:article_id", commonController.Bookmark)
	// Remove article from bookmark list
	group.DELETE("/bookmarks/:bookmark_id/:article_id/", commonController.Unbookmark)

	// Get all subscriptions
	group.GET("/subscriptions", commonController.GetSubscriptions)
	// Get whether user is subscribed to publisher
	group.GET("/subscriptions/:publisher_id", commonController.IsSubscribed)
	// Subscribe to publisher
	group.PUT("/subscriptions/:publisher_id", commonController.Subscribe)
	// Unsubscribe from publisher
	group.DELETE("/subscriptions/:publisher_id", commonController.Unsubscribe)
}
