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

	GetBookmarkLists(userId uint32) ([]*messages.BookmarkList, error)
	GetBookmarkListById(id uint32, userId uint32) (*messages.BookmarkList, error)
	IsBookmarked(articleId uint32, bookmarkListId uint32) (bool, error)
	Bookmark(articleId uint32, bookmarkListId uint32, userId uint32) error
	Unbookmark(articleId uint32, bookmarkListId uint32, userId uint32) error

	GetSubscriptions(userId uint32) ([]*messages.Publisher, error)
	IsSubscribed(publisherId uint32, userId uint32) (bool, error)
	Subscribe(publisherId uint32, userId uint32) error
	Unsubscribe(publisherId uint32, userId uint32) error
}

func (controller *CommonController) GetArticleMetadataById(ctx *gin.Context) {
	articleId, err := strconv.Atoi(ctx.Param("article_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	metadata, err := controller.CommonUsecase.GetArticleMetadataById(uint32(articleId), uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, metadata)
}

func (controller *CommonController) GetPublisherById(ctx *gin.Context) {
	publisherId, err := strconv.Atoi(ctx.Param("publisher_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	publisher, err := controller.CommonUsecase.GetPublisherById(uint32(publisherId), uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, publisher)
}

func (controller *CommonController) GetBookmarkLists(ctx *gin.Context) {
	userId := ctx.GetInt64(api.UserIdKey)
	bookmarkLists, err := controller.CommonUsecase.GetBookmarkLists(uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, bookmarkLists)
}

func (controller *CommonController) GetBookmarkListById(ctx *gin.Context) {
	bookmarkListId, err := strconv.Atoi(ctx.Param("bookmark_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	bookmarkList, err := controller.CommonUsecase.GetBookmarkListById(uint32(bookmarkListId), uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, bookmarkList)
}

func (controller *CommonController) GetSubscriptions(ctx *gin.Context) {
	userId := ctx.GetInt64(api.UserIdKey)
	subscriptions, err := controller.CommonUsecase.GetSubscriptions(uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, subscriptions)
}

func (controller *CommonController) IsBookmarked(ctx *gin.Context) {
	articleId, err := strconv.Atoi(ctx.Param("article_id"))
	bookmarkListId, err := strconv.Atoi(ctx.Param("bookmark_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	isBookmarked, err := controller.CommonUsecase.IsBookmarked(uint32(articleId), uint32(bookmarkListId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.SimpleResponse{Message: strconv.FormatBool(isBookmarked)})
}

func (controller *CommonController) IsSubscribed(ctx *gin.Context) {
	publisherId, err := strconv.Atoi(ctx.Param("publisher_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt64(api.UserIdKey)
	isSubscribed, err := controller.CommonUsecase.IsSubscribed(uint32(publisherId), uint32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.SimpleResponse{Message: strconv.FormatBool(isSubscribed)})
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
