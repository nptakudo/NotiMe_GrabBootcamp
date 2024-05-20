package controller

import (
	"context"
	"github.com/gin-gonic/gin"
	"log/slog"
	"net/http"
	"notime/api"
	"notime/api/messages"
	"notime/bootstrap"
	"notime/domain"
	"notime/repository"
	"strconv"
	"strings"
)

type CommonController struct {
	CommonUsecase CommonUsecase
	Env           *bootstrap.Env
}

type CommonUsecase interface {
	GetArticleMetadataById(ctx context.Context, id int64, userId int32) (*messages.ArticleMetadata, error)
	GetPublisherById(ctx context.Context, id int32, userId int32) (*messages.Publisher, error)

	GetBookmarkLists(ctx context.Context, userId int32) ([]*messages.BookmarkList, error)
	GetBookmarkListById(ctx context.Context, id int32, userId int32) (*messages.BookmarkList, error)
	IsBookmarked(ctx context.Context, articleId int64, bookmarkListId int32) (bool, error)
	Bookmark(ctx context.Context, articleId int64, bookmarkListId int32, userId int32) error
	Unbookmark(ctx context.Context, articleId int64, bookmarkListId int32, userId int32) error

	GetSubscriptions(ctx context.Context, userId int32) ([]*messages.Publisher, error)
	IsSubscribed(ctx context.Context, publisherId int32, userId int32) (bool, error)
	Subscribe(ctx context.Context, publisherId int32, userId int32) error
	Unsubscribe(ctx context.Context, publisherId int32, userId int32) error

	SearchPublisher(ctx context.Context, searchQuery string, userId int) ([]*messages.Publisher, error)

	GetArticlesByPublisher(ctx context.Context, publisherId int32, userId int32, count int, offset int) ([]*messages.ArticleMetadata, error)
}

func (controller *CommonController) GetArticleMetadataById(ctx *gin.Context) {
	articleId, err := strconv.Atoi(ctx.Param("article_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt(api.UserIdKey)
	metadata, err := controller.CommonUsecase.GetArticleMetadataById(ctx, int64(articleId), int32(userId))
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

	userId := ctx.GetInt(api.UserIdKey)
	publisher, err := controller.CommonUsecase.GetPublisherById(ctx, int32(publisherId), int32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, publisher)
}

func (controller *CommonController) GetBookmarkLists(ctx *gin.Context) {
	userId := ctx.GetInt(api.UserIdKey)
	bookmarkLists, err := controller.CommonUsecase.GetBookmarkLists(ctx, int32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	slog.Info("[CommonController] GetBookmarkLists: respond with:", "length", len(bookmarkLists))
	ctx.JSON(http.StatusOK, bookmarkLists)
}

func (controller *CommonController) GetBookmarkListById(ctx *gin.Context) {
	bookmarkListId, err := strconv.Atoi(ctx.Param("bookmark_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt(api.UserIdKey)
	bookmarkList, err := controller.CommonUsecase.GetBookmarkListById(ctx, int32(bookmarkListId), int32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, bookmarkList)
}

func (controller *CommonController) GetSubscriptions(ctx *gin.Context) {
	// get user id from path parameter
	userId := ctx.GetInt(api.UserIdKey)
	slog.Info("[CommonController] GetSubscriptions: user id:", "userId", userId)
	subscriptions, err := controller.CommonUsecase.GetSubscriptions(ctx, int32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	slog.Info("[CommonController] GetSubscriptions: respond with:", "length", len(subscriptions))
	ctx.JSON(http.StatusOK, subscriptions)
}

func (controller *CommonController) IsBookmarked(ctx *gin.Context) {
	articleId, err := strconv.Atoi(ctx.Param("article_id"))
	bookmarkListId, err := strconv.Atoi(ctx.Param("bookmark_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	isBookmarked, err := controller.CommonUsecase.IsBookmarked(ctx, int64(articleId), int32(bookmarkListId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	slog.Info("[CommonController] IsBookmarked: respond with:", "isBookmarked", isBookmarked)
	ctx.JSON(http.StatusOK, messages.SimpleResponse{Message: strconv.FormatBool(isBookmarked)})
}

func (controller *CommonController) IsSubscribed(ctx *gin.Context) {
	publisherId, err := strconv.Atoi(ctx.Param("publisher_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt(api.UserIdKey)
	isSubscribed, err := controller.CommonUsecase.IsSubscribed(ctx, int32(publisherId), int32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	slog.Info("[CommonController] IsSubscribed: respond with:", "isSubscribed", isSubscribed)
	ctx.JSON(http.StatusOK, messages.SimpleResponse{Message: strconv.FormatBool(isSubscribed)})
}

func (controller *CommonController) Bookmark(ctx *gin.Context) {
	articleId, err := strconv.Atoi(ctx.Param("article_id"))
	bookmarkListId, err := strconv.Atoi(ctx.Param("bookmark_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	userId := ctx.GetInt(api.UserIdKey)
	err = controller.CommonUsecase.Bookmark(ctx, int64(articleId), int32(bookmarkListId), int32(userId))
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

	userId := ctx.GetInt(api.UserIdKey)
	err = controller.CommonUsecase.Unbookmark(ctx, int64(articleId), int32(bookmarkListId), int32(userId))
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

	userId := ctx.GetInt(api.UserIdKey)
	err = controller.CommonUsecase.Subscribe(ctx, int32(publisherId), int32(userId))
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

	userId := ctx.GetInt(api.UserIdKey)
	err = controller.CommonUsecase.Unsubscribe(ctx, int32(publisherId), int32(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, nil)
}

type SearchResponse struct {
	IsExisting bool                      `json:"is_existing"`
	Articles   []*domain.ArticleMetadata `json:"articles"`
	Publishers []*messages.Publisher     `json:"publishers"`
}

func (controller *CommonController) SearchPublisher(ctx *gin.Context) { // if the search query is not in the db, call the scrape data
	searchQuery := ctx.Query("query")
	userId := ctx.GetInt(api.UserIdKey)

	webscrapeRepo := repository.NewWebscrapeRepository(controller.Env)

	if searchQuery == "" {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: "search query cannot be empty"})
		return
	}
	// search for publishers in db
	publishers, err := controller.CommonUsecase.SearchPublisher(ctx, searchQuery, int(userId))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}
	// if no publishers found in db, scrape the data
	if publishers == nil || len(publishers) == 0 {
		if !strings.HasPrefix(searchQuery, "https://") {
			ctx.JSON(http.StatusAccepted, messages.SimpleResponse{Message: "No publishers found"})
			return
		}
		if !strings.HasSuffix(searchQuery, "/") {
			searchQuery += "/"
		}
		// scrape data
		articles, _, err := webscrapeRepo.ScrapeFromUrl(searchQuery)
		if err != nil {
			ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
			return
		}

		ctx.JSON(http.StatusOK, SearchResponse{
			IsExisting: false,
			Articles:   articles,
			Publishers: make([]*messages.Publisher, 0),
		})
		return
	}

	ctx.JSON(http.StatusOK, SearchResponse{
		IsExisting: true,
		Articles:   make([]*domain.ArticleMetadata, 0),
		Publishers: publishers,
	})
}

func (controller *CommonController) GetArticlesByPublisher(ctx *gin.Context) {
	publisherId, err := strconv.Atoi(ctx.Param("publisher_id"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}
	userId := ctx.GetInt64(api.UserIdKey)
	count, err := strconv.Atoi(ctx.Query("count"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}
	offset, err := strconv.Atoi(ctx.Query("offset"))
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	articles, err := controller.CommonUsecase.GetArticlesByPublisher(ctx, int32(publisherId), int32(userId), count, offset)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, articles)
}
