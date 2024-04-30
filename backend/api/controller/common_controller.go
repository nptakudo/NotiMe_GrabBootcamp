package controller

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"notime/api"
	"notime/api/messages"
	"strconv"
)

type CommonController struct {
	CommonUsecase CommonUsecase
}

type CommonUsecase interface {
	GetArticleMetadataById(id uint32, userId uint32) (*messages.ArticleMetadata, error)
	GetPublisherById(id uint32, userId uint32) (*messages.Publisher, error)

	IsBookmarked(articleId uint32, bookmarkListId uint32) (bool, error)
	IsSubscribed(publisherId uint32, userId uint32) (bool, error)

	Bookmark(articleId uint32, bookmarkListId uint32, userId uint32) error
	Unbookmark(articleId uint32, bookmarkListId uint32, userId uint32) error
	Subscribe(publisherId uint32, userId uint32) error
	Unsubscribe(publisherId uint32, userId uint32) error
}

// TODO
func (controller *CommonController) GetArticleMetadataById(ctx *gin.Context) {

}

func (controller *CommonController) GetPublisherById(ctx *gin.Context) {

}

func (controller *CommonController) IsBookmarked(ctx *gin.Context) {

}

func (controller *CommonController) IsSubscribed(ctx *gin.Context) {

}

func (controller *CommonController) Bookmark(ctx *gin.Context) {
	articleId, err := strconv.Atoi(ctx.Param("article_id"))
	bookmarkListId, err := strconv.Atoi(ctx.Param("bookmark_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	err = controller.CommonUsecase.Bookmark(uint32(articleId), uint32(bookmarkListId), uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, nil)
}

func (controller *CommonController) Unbookmark(ctx *gin.Context) {
	articleId, err := strconv.Atoi(ctx.Param("article_id"))
	bookmarkListId, err := strconv.Atoi(ctx.Param("bookmark_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	err = controller.CommonUsecase.Unbookmark(uint32(articleId), uint32(bookmarkListId), uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, nil)
}

func (controller *CommonController) Subscribe(ctx *gin.Context) {
	publisherId, err := strconv.Atoi(ctx.Param("publisher_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	err = controller.CommonUsecase.Subscribe(uint32(publisherId), uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, nil)
}

func (controller *CommonController) Unsubscribe(ctx *gin.Context) {
	publisherId, err := strconv.Atoi(ctx.Param("publisher_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	err = controller.CommonUsecase.Unsubscribe(uint32(publisherId), uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, nil)
}
