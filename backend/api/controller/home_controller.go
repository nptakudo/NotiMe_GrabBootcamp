package controller

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"notime/api"
	"notime/api/messages"
)

type HomeController struct {
	HomeUsecase messages.HomeUsecase
}

func (controller *HomeController) GetLatestSubscribedArticles(ctx *gin.Context) {
	var request messages.ArticlesRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}
	userID := ctx.GetInt64(api.UserIDKey)
	articles, err := controller.HomeUsecase.GetLatestSubscribedArticles(request.Count, uint32(userID))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.ArticlesResponse{Articles: articles})
}

func (controller *HomeController) GetLatestSubscribedArticlesByPublisher(ctx *gin.Context) {
	var request messages.ArticlesRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userID := ctx.GetInt64(api.UserIDKey)
	articles, err := controller.HomeUsecase.GetLatestSubscribedArticlesByPublisher(request.Count, uint32(userID))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.ArticlesResponse{Articles: articles})
}

func (controller *HomeController) GetExploreArticles(ctx *gin.Context) {
	var request messages.ArticlesRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userID := ctx.GetInt64(api.UserIDKey)
	articles, err := controller.HomeUsecase.GetExploreArticles(request.Count, uint32(userID))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.ArticlesResponse{Articles: articles})
}

func (controller *HomeController) Search(ctx *gin.Context) {
	var request messages.SearchRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userID := ctx.GetInt64(api.UserIDKey)
	articles, err := controller.HomeUsecase.Search(request.Query, request.Count, uint32(userID))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.ArticlesResponse{Articles: articles})
}

func (controller *HomeController) Bookmark(ctx *gin.Context) {
	var request messages.BookmarkRelatedRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userID := ctx.GetInt64(api.UserIDKey)
	err := controller.HomeUsecase.Bookmark(request.ArticleID, request.BookmarkListId, uint32(userID))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, nil)
}

func (controller *HomeController) Unbookmark(ctx *gin.Context) {
	var request messages.BookmarkRelatedRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userID := ctx.GetInt64(api.UserIDKey)
	err := controller.HomeUsecase.Unbookmark(request.ArticleID, request.BookmarkListId, uint32(userID))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, nil)
}

func (controller *HomeController) Subscribe(ctx *gin.Context) {
	var request messages.SubscribeRelatedRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userID := ctx.GetInt64(api.UserIDKey)
	err := controller.HomeUsecase.Subscribe(request.PublisherID, uint32(userID))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, nil)
}

func (controller *HomeController) Unsubscribe(ctx *gin.Context) {
	var request messages.SubscribeRelatedRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userID := ctx.GetInt64(api.UserIDKey)
	err := controller.HomeUsecase.Unsubscribe(request.PublisherID, uint32(userID))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, nil)
}
