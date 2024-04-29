package messages

import "notime/domain"

type ArticleMetadata struct {
	IsBookmarked bool `json:"is_bookmarked"`
	domain.ArticleMetadata
}

type ArticleContent struct {
	Id       uint32 `json:"id"`
	Content  string `json:"content"`
	ImageUrl string `json:"image_url"`
}

type Publisher struct {
	IsSubscribed bool `json:"is_subscribed"`
	domain.Publisher
}
