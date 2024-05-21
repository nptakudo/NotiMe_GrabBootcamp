package route

import (
	"github.com/gin-gonic/gin"
	"notime/api/controller"
	"notime/bootstrap"
	"notime/external/sql/store"
	"notime/usecases"
)

func NewReaderRouter(group *gin.RouterGroup, env *bootstrap.Env, db *store.Queries) {
	readerController := controller.ReaderController{
		ReaderUsecase: usecases.NewReaderUsecase(env, db),
	}
	// Get article metadata and content by article id
	group.GET("/:article_id", readerController.GetArticleById)
	// Get related articles metadata by article id
	// Query params: count, offset
	group.GET("/:article_id/related_articles", readerController.GetRelatedArticles)
	group.GET("/new_article", readerController.GetNewArticle)
}
