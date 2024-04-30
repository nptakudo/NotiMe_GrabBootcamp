package controller

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"notime/api"
	"notime/api/messages"
	"strconv"
)

type ReaderController struct {
	ReaderUsecase ReaderUsecase
}

type ReaderUsecase interface {
	GetArticleById(id uint32, userId uint32) (*messages.ArticleResponse, error)
	GetRelatedArticles(articleId uint32, userId uint32, count int, offset int) (*messages.RelatedArticlesResponse, error)
}

func (controller *ReaderController) GetArticleById(ctx *gin.Context) {
	reqArticleId, err := strconv.Atoi(ctx.Param("article_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}
	userId := ctx.GetInt64(api.UserIdKey)
	article, err := controller.ReaderUsecase.GetArticleById(uint32(reqArticleId), uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, article)
}

func (controller *ReaderController) GetRelatedArticles(ctx *gin.Context) {
	reqArticleId, err := strconv.Atoi(ctx.Param("article_id"))
	reqCount, err := strconv.Atoi(ctx.DefaultQuery("count", "-1"))
	reqOffset, err := strconv.Atoi(ctx.DefaultQuery("offset", "0"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	articles, err := controller.ReaderUsecase.GetRelatedArticles(uint32(reqArticleId), uint32(userId), reqCount, reqOffset)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, articles)
}
