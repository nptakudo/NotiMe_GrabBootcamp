package route

import (
	"github.com/gin-gonic/gin"
	"notime/api/controller"
	"notime/repository"
	"notime/usecases"
)

func NewReaderController(group *gin.RouterGroup) {
	readerController := controller.ReaderController{
		ReaderUsecase: &usecases.ReaderUsecaseImpl{
			RecsysRepository:       &repository.RecsysRepositoryImpl{},
			BookmarkListRepository: &repository.BookmarkListRepositoryImpl{},
			CommonUsecase:          &usecases.CommonUsecaseImpl{},
		},
	}
	group.GET("/article", readerController.GetArticleById)
	group.POST("/bookmark", readerController.Bookmark)
	group.POST("/unbookmark", readerController.Unbookmark)
	group.POST("/subscribe", readerController.Subscribe)
	group.POST("/unsubscribe", readerController.Unsubscribe)
	group.GET("/related_articles", readerController.GetRelatedArticles)
}
