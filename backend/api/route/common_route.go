package route

import (
	"github.com/gin-gonic/gin"
	"notime/api/controller"
	"notime/bootstrap"
	"notime/external/sql/store"
	"notime/usecases"
)

func NewCommonRouter(group *gin.RouterGroup, env *bootstrap.Env, db *store.Queries) {
	commonController := controller.CommonController{
		CommonUsecase: usecases.NewCommonUsecase(env, db),
		Env:           env,
	}
	// Get publisher by id
	group.GET("/publisher/:publisher_id", commonController.GetPublisherById)

	// Get all bookmark lists
	group.GET("/bookmarks", commonController.GetBookmarkLists)
	// Create new bookmark list
	group.POST("/bookmarks", commonController.CreateBookmarkList)
	// Delete bookmark list
	group.DELETE("/bookmarks/:bookmark_id", commonController.DeleteBookmarkList)
	// Get bookmark list by id
	group.GET("/bookmarks/:bookmark_id", commonController.GetBookmarkListById)
	// Get whether article is bookmarked
	group.GET("/bookmarks/:bookmark_id/:article_id", commonController.IsBookmarked)
	// Add article to bookmark list
	group.PUT("/bookmarks/:bookmark_id/:article_id", commonController.Bookmark)
	// Remove article from bookmark list
	group.DELETE("/bookmarks/:bookmark_id/:article_id", commonController.Unbookmark)

	// Get all subscriptions
	group.GET("/subscriptions", commonController.GetSubscriptions)
	// Get whether user is subscribed to publisher
	group.GET("/subscriptions/publisher/:publisher_id", commonController.IsSubscribed)
	// Search a publisher
	group.GET("/subscriptions/search/", commonController.SearchPublisher)
	// Subscribe to publisher
	group.PUT("/subscriptions/:publisher_id", commonController.Subscribe)
	// Unsubscribe from publisher
	group.DELETE("/subscriptions/:publisher_id", commonController.Unsubscribe)
	// Get all articles from a publisher
	group.GET("/articles/publisher/:publisher_id", commonController.GetArticlesByPublisher)
	group.POST("/add_new_source", commonController.AddNewSource)
}
