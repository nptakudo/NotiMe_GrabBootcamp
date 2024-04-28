package controller

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"notime/api/models"
)

type HomeController struct {
	HomeUsecase models.HomeUsecase
}

func (controller *HomeController) GetSubscribedArticlesByDate(ctx *gin.Context) {
	var request models.ArticlesRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, models.SimpleResponse{Message: err.Error()})
		return
	}

	articles, err := controller.HomeUsecase.GetSubscribedArticlesByDate(request.Count, request.UserId)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, models.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, models.ArticlesResponse{Articles: articles})
}

func (controller *HomeController) GetSubscribedArticlesByPublisher(ctx *gin.Context) {
	var request models.ArticlesRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, models.SimpleResponse{Message: err.Error()})
		return
	}

	articles, err := controller.HomeUsecase.GetSubscribedArticlesByPublisher(request.Count, request.UserId)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, models.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, models.ArticlesResponse{Articles: articles})
}

func (controller *HomeController) GetExploreArticles(ctx *gin.Context) {
	var request models.ArticlesRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, models.SimpleResponse{Message: err.Error()})
		return
	}

	articles, err := controller.HomeUsecase.GetExploreArticles(request.Count, request.UserId)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, models.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, models.ArticlesResponse{Articles: articles})
}

func (controller *HomeController) Search(ctx *gin.Context) {
	var request models.SearchRequest
	if err := ctx.ShouldBind(&request); err != nil {
		ctx.JSON(http.StatusBadRequest, models.SimpleResponse{Message: err.Error()})
		return
	}

	articles, err := controller.HomeUsecase.Search(request.Query, request.Count, request.UserId)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, models.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, models.ArticlesResponse{Articles: articles})
}
