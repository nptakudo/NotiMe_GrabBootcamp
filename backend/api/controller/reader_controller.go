package controller

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"notime/api"
	"notime/api/messages"
)

type ReaderController struct {
	ReaderUsecase ReaderUsecase
}

type ReaderUsecase interface {
	GetArticleById(id uint32, userId uint32) (*messages.ArticleResponse, error)
	Bookmark(bookmarkListId uint32, articleId uint32, userId uint32) error
	Unbookmark(bookmarkListId uint32, articleId uint32, userId uint32) error
	Subscribe(publisherId uint32, userId uint32) error
	Unsubscribe(publisherId uint32, userId uint32) error
	GetRelatedArticles(articleId uint32, userId uint32, count int) (*messages.RelatedArticlesResponse, error)
}

func (controller *ReaderController) GetArticleById(ctx *gin.Context) {
	var request messages.ArticleRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}
	userId := ctx.GetInt64(api.UserIdKey)
	article, err := controller.ReaderUsecase.GetArticleById(request.Id, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, article)
}

func (controller *ReaderController) Bookmark(ctx *gin.Context) {
	var request messages.BookmarkRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}
	userId := ctx.GetInt64(api.UserIdKey)
	err := controller.ReaderUsecase.Bookmark(request.BookmarkListId, request.ArticleId, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.SimpleResponse{Message: "Bookmarked successfully"})
}

func (controller *ReaderController) Unbookmark(ctx *gin.Context) {
	var request messages.BookmarkRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}
	userId := ctx.GetInt64(api.UserIdKey)
	err := controller.ReaderUsecase.Unbookmark(request.BookmarkListId, request.ArticleId, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.SimpleResponse{Message: "Unbookmarked successfully"})
}

func (controller *ReaderController) Subscribe(ctx *gin.Context) {
	var request messages.SubscribeRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}
	userId := ctx.GetInt64(api.UserIdKey)
	err := controller.ReaderUsecase.Subscribe(request.PublisherId, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.SimpleResponse{Message: "Subscribed successfully"})
}

func (controller *ReaderController) Unsubscribe(ctx *gin.Context) {
	var request messages.SubscribeRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}
	userId := ctx.GetInt64(api.UserIdKey)
	err := controller.ReaderUsecase.Unsubscribe(request.PublisherId, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.SimpleResponse{Message: "Unsubscribed successfully"})
}

func (controller *ReaderController) GetRelatedArticles(ctx *gin.Context) {
	var request messages.RelatedArticlesRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}
	userId := ctx.GetInt64(api.UserIdKey)
	articles, err := controller.ReaderUsecase.GetRelatedArticles(request.ArticleId, uint32(userId), request.Count)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, articles)
}
