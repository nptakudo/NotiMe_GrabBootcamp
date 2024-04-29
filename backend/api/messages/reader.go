package messages

type ArticleRequest struct {
	Id uint32 `uri:"id" binding:"required"`
}

type ArticleResponse struct {
	Metadata *ArticleMetadata `json:"article"`
	Content  *ArticleContent  `json:"content"`
}

type RelatedArticlesRequest struct {
	ArticleId uint32 `uri:"article_id" binding:"required"`
	Count     int    `form:"count" binding:"required"`
}

type RelatedArticlesResponse struct {
	Articles []*ArticleMetadata `json:"articles"`
}
