package messages

import "notime/domain"

type ArticleMetadata struct {
	IsBookmarked bool `json:"is_bookmarked"`
	domain.ArticleMetadata
}

type ArticleContent struct {
	Id      int64  `json:"id"`
	Content string `json:"content"`
}

type Publisher struct {
	IsSubscribed bool `json:"is_subscribed"`
	domain.Publisher
}

type BookmarkList struct {
	Id       int32              `json:"id"`
	Name     string             `json:"name"`
	IsSaved  bool               `json:"is_saved"`
	Articles []*ArticleMetadata `json:"articles"`
	OwnerId  int32              `json:"owner_id"`
}

type User struct {
	Id       int32  `json:"id"`
	Username string `json:"username"`
	Password string `json:"password"`
	Token    string `json:"token"`
}
