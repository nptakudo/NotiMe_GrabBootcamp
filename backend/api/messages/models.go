package messages

import "notime/domain"

type Article struct {
	IsBookmarked bool `json:"is_bookmarked"`
	domain.Article
}

type Publisher struct {
	IsSubscribed bool `json:"is_subscribed"`
	domain.Publisher
}
