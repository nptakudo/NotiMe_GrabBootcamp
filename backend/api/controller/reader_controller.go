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

type ReaderController struct {
	ReaderUsecase ReaderUsecase
}

type ReaderUsecase interface {
	GetArticleById(ctx context.Context, id int64, userId int32) (*messages.ArticleResponse, error)
	GetRelatedArticles(ctx context.Context, articleId int64, userId int32, count int, offset int) ([]*messages.ArticleMetadata, error)
}

func (controller *ReaderController) GetArticleById(ctx *gin.Context) {
	reqArticleId, err := strconv.Atoi(ctx.Param("article_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}
	userId := ctx.GetInt(api.UserIdKey)
	article, err := controller.ReaderUsecase.GetArticleById(ctx, int64(reqArticleId), int32(userId))
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

	userId := ctx.GetInt(api.UserIdKey)
	articles, err := controller.ReaderUsecase.GetRelatedArticles(ctx, int64(reqArticleId), int32(userId), reqCount, reqOffset)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	slog.Info("[ReaderController] GetRelatedArticles: respond with:", "length", len(articles))
	ctx.JSON(http.StatusOK, articles)
}
