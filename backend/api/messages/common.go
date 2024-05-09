package messages

type ArticleListRequest struct {
	Count int `form:"count" binding:"required"`
}

type ArticleListResponse struct {
	Articles []*ArticleMetadata `json:"articles"`
}

type SearchRequest struct {
	Query string `form:"query" binding:"required"`
	Count int    `form:"count" binding:"required"`
}

type BookmarkRequest struct {
	ArticleId      int64 `form:"article_id" binding:"required"`
	BookmarkListId int32 `form:"bookmark_list_id" binding:"required"`
}

type SubscribeRequest struct {
	PublisherId int32 `form:"publisher_id" binding:"required"`
}
