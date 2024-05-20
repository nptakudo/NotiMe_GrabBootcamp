package controller

import (
	"context"
	"github.com/gin-gonic/gin"
	"log/slog"
	"net/http"
	"notime/api"
	"notime/api/messages"
	"strconv"
)

type HomeController struct {
	HomeUsecase HomeUsecase
}

type HomeUsecase interface {
	GetSubscribedPublishers(ctx context.Context, userId int32) ([]*messages.Publisher, error)
	GetLatestSubscribedArticles(ctx context.Context, count int, offset int, userId int32) ([]*messages.ArticleMetadata, error)
	GetLatestSubscribedArticlesByPublisher(ctx context.Context, countEachPublisher int, offset int, userId int32) ([]*messages.ArticleMetadata, error)
	GetExploreArticles(ctx context.Context, count int, offset int, userId int32) ([]*messages.ArticleMetadata, error)
	Search(ctx context.Context, query string, count int, offset int, userId int32) ([]*messages.ArticleMetadata, error)
}

func (controller *HomeController) GetLatestSubscribedArticles(ctx *gin.Context) {
	reqCount, err := strconv.Atoi(ctx.DefaultQuery("count", "-1"))
	reqOffset, err := strconv.Atoi(ctx.DefaultQuery("offset", "0"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}
	userId := ctx.GetInt(api.UserIdKey)
	articles, err := controller.HomeUsecase.GetLatestSubscribedArticles(ctx, reqCount, reqOffset, int32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	slog.Info("[HomeController] GetLatestSubscribedArticles: respond with:", "length", len(articles))
	ctx.JSON(http.StatusOK, articles)
}

func (controller *HomeController) GetLatestSubscribedArticlesByPublisher(ctx *gin.Context) {
	reqCount, err := strconv.Atoi(ctx.DefaultQuery("count", "-1"))
	reqOffset, err := strconv.Atoi(ctx.DefaultQuery("offset", "0"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt(api.UserIdKey)
	articles, err := controller.HomeUsecase.GetLatestSubscribedArticlesByPublisher(ctx, reqCount, reqOffset, int32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	slog.Info("[HomeController] GetLatestSubscribedArticlesByPublisher: respond with:", "length", len(articles))
	ctx.JSON(http.StatusOK, articles)
}

func (controller *HomeController) GetExploreArticles(ctx *gin.Context) {
	reqCount, err := strconv.Atoi(ctx.DefaultQuery("count", "-1"))
	reqOffset, err := strconv.Atoi(ctx.DefaultQuery("offset", "0"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt(api.UserIdKey)
	articles, err := controller.HomeUsecase.GetExploreArticles(ctx, reqCount, reqOffset, int32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	slog.Info("[HomeController] GetExploreArticles: respond with:", "length", len(articles))
	ctx.JSON(http.StatusOK, articles)
}

func (controller *HomeController) Search(ctx *gin.Context) {
	reqQuery := ctx.Param("query")
	reqCount, err := strconv.Atoi(ctx.DefaultQuery("count", "-1"))
	reqOffset, err := strconv.Atoi(ctx.DefaultQuery("offset", "0"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt(api.UserIdKey)
	articles, err := controller.HomeUsecase.Search(ctx, reqQuery, reqCount, reqOffset, int32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	slog.Info("[HomeController] Search: respond with:", "length", len(articles))
	ctx.JSON(http.StatusOK, articles)
}
