package messages

type ArticleRequest struct {
	Id int32 `uri:"id" binding:"required"`
}

type ArticleResponse struct {
	Metadata *ArticleMetadata `json:"metadata"`
	Content  *ArticleContent  `json:"content"`
	Summary  string           `json:"summary"`
}

type RelatedArticlesRequest struct {
	ArticleId int64 `uri:"article_id" binding:"required"`
	Count     int   `form:"count" binding:"required"`
}
