package route

import (
	"github.com/gin-gonic/gin"
	"notime/api/controller"
	"notime/repository"
	"notime/usecases"
)

func NewReaderRouter(group *gin.RouterGroup) {
	readerController := controller.ReaderController{
		ReaderUsecase: &usecases.ReaderUsecaseImpl{
			RecsysRepository:       &repository.RecsysRepositoryImpl{},
			BookmarkListRepository: &repository.BookmarkListRepositoryImpl{},
			CommonUsecase:          &usecases.CommonUsecaseImpl{},
		},
	}
	// Get article metadata and content by article id
	group.GET("/:article_id", readerController.GetArticleById)
	// Get related articles metadata by article id
	// Query params: count, offset
	group.GET("/:article_id/related_articles", readerController.GetRelatedArticles)
}
