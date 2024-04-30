package controller

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"notime/api"
	"notime/api/messages"
	"strconv"
)

type HomeController struct {
	HomeUsecase HomeUsecase
}

type HomeUsecase interface {
	GetSubscribedPublishers(userId uint32) ([]*messages.Publisher, error)
	GetLatestSubscribedArticles(count int, offset int, userId uint32) ([]*messages.ArticleMetadata, error)
	GetLatestSubscribedArticlesByPublisher(countEachPublisher int, offset int, userId uint32) ([]*messages.ArticleMetadata, error)
	GetExploreArticles(count int, offset int, userId uint32) ([]*messages.ArticleMetadata, error)
	Search(query string, count int, offset int, userId uint32) ([]*messages.ArticleMetadata, error)
}

func (controller *HomeController) GetLatestSubscribedArticles(ctx *gin.Context) {
	reqCount, err := strconv.Atoi(ctx.DefaultQuery("count", "-1"))
	reqOffset, err := strconv.Atoi(ctx.DefaultQuery("offset", "0"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}
	userId := ctx.GetInt64(api.UserIdKey)
	articles, err := controller.HomeUsecase.GetLatestSubscribedArticles(reqCount, reqOffset, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.ArticleListResponse{Articles: articles})
}

func (controller *HomeController) GetLatestSubscribedArticlesByPublisher(ctx *gin.Context) {
	reqCount, err := strconv.Atoi(ctx.DefaultQuery("count", "-1"))
	reqOffset, err := strconv.Atoi(ctx.DefaultQuery("offset", "0"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	articles, err := controller.HomeUsecase.GetLatestSubscribedArticlesByPublisher(reqCount, reqOffset, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.ArticleListResponse{Articles: articles})
}

func (controller *HomeController) GetExploreArticles(ctx *gin.Context) {
	reqCount, err := strconv.Atoi(ctx.DefaultQuery("count", "-1"))
	reqOffset, err := strconv.Atoi(ctx.DefaultQuery("offset", "0"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	articles, err := controller.HomeUsecase.GetExploreArticles(reqCount, reqOffset, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.ArticleListResponse{Articles: articles})
}

func (controller *HomeController) Search(ctx *gin.Context) {
	reqQuery := ctx.DefaultQuery("query", "")
	reqCount, err := strconv.Atoi(ctx.DefaultQuery("count", "-1"))
	reqOffset, err := strconv.Atoi(ctx.DefaultQuery("offset", "0"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	articles, err := controller.HomeUsecase.Search(reqQuery, reqCount, reqOffset, uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.ArticleListResponse{Articles: articles})
}
