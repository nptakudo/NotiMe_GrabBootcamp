package controller

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"notime/api"
	"notime/api/messages"
)

type HomeController struct {
	HomeUsecase HomeUsecase
}

type HomeUsecase interface {
	GetSubscribedPublishers(userId uint32) ([]*messages.Publisher, error)
	GetLatestSubscribedArticles(count int, userId uint32) ([]*messages.ArticleMetadata, error)
	GetLatestSubscribedArticlesByPublisher(countEachPublisher int, userId uint32) ([]*messages.ArticleMetadata, error)
	GetExploreArticles(count int, userId uint32) ([]*messages.ArticleMetadata, error)
	Search(query string, count int, userId uint32) ([]*messages.ArticleMetadata, error)

	Bookmark(articleId uint32, bookmarkListId uint32, userId uint32) error
	Unbookmark(articleId uint32, bookmarkListId uint32, userId uint32) error
	Subscribe(publisherId uint32, userId uint32) error
	Unsubscribe(publisherId uint32, userId uint32) error
}

func (controller *HomeController) GetLatestSubscribedArticles(ctx *gin.Context) {
	var request messages.ArticleListRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}
	userId := ctx.GetInt64(api.UserIdKey)
	articles, err := controller.HomeUsecase.GetLatestSubscribedArticles(request.Count, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.ArticleListResponse{Articles: articles})
}

func (controller *HomeController) GetLatestSubscribedArticlesByPublisher(ctx *gin.Context) {
	var request messages.ArticleListRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	articles, err := controller.HomeUsecase.GetLatestSubscribedArticlesByPublisher(request.Count, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.ArticleListResponse{Articles: articles})
}

func (controller *HomeController) GetExploreArticles(ctx *gin.Context) {
	var request messages.ArticleListRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	articles, err := controller.HomeUsecase.GetExploreArticles(request.Count, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.ArticleListResponse{Articles: articles})
}

func (controller *HomeController) Search(ctx *gin.Context) {
	var request messages.SearchRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	articles, err := controller.HomeUsecase.Search(request.Query, request.Count, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.ArticleListResponse{Articles: articles})
}

func (controller *HomeController) Bookmark(ctx *gin.Context) {
	var request messages.BookmarkRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	err := controller.HomeUsecase.Bookmark(request.ArticleId, request.BookmarkListId, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, nil)
}

func (controller *HomeController) Unbookmark(ctx *gin.Context) {
	var request messages.BookmarkRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	err := controller.HomeUsecase.Unbookmark(request.ArticleId, request.BookmarkListId, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, nil)
}

func (controller *HomeController) Subscribe(ctx *gin.Context) {
	var request messages.SubscribeRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	err := controller.HomeUsecase.Subscribe(request.PublisherId, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, nil)
}

func (controller *HomeController) Unsubscribe(ctx *gin.Context) {
	var request messages.SubscribeRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	err := controller.HomeUsecase.Unsubscribe(request.PublisherId, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, nil)
}
