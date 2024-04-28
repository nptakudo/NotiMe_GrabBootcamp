package messages

type ArticlesRequest struct {
	Count int `form:"count" binding:"required"`
}

type SearchRequest struct {
	Query string `form:"query" binding:"required"`
	Count int    `form:"count" binding:"required"`
}

type BookmarkRelatedRequest struct {
	ArticleID      uint32 `form:"article_id" binding:"required"`
	BookmarkListId uint32 `form:"bookmark_list_id" binding:"required"`
}

type SubscribeRelatedRequest struct {
	PublisherID uint32 `form:"publisher_id" binding:"required"`
}

type ArticlesResponse struct {
	Articles []*Article `json:"articles"`
}

type ArticleRelatedResponse struct {
	Article *Article `json:"article"`
}

type PublisherRelatedResponse struct {
	Publisher *Publisher `json:"publisher"`
}

type HomeUsecase interface {
	GetSubscribedPublishers(userId uint32) ([]*Publisher, error)
	GetLatestSubscribedArticles(count int, userId uint32) ([]*Article, error)
	GetLatestSubscribedArticlesByPublisher(countEachPublisher int, userId uint32) ([]*Article, error)
	GetExploreArticles(count int, userId uint32) ([]*Article, error)
	Search(query string, count int, userId uint32) ([]*Article, error)

	Bookmark(articleId uint32, bookmarkListId uint32, userId uint32) error
	Unbookmark(articleId uint32, bookmarkListId uint32, userId uint32) error
	Subscribe(publisherId uint32, userId uint32) error
	Unsubscribe(publisherId uint32, userId uint32) error
}
