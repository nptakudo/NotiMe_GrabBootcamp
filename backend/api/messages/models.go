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

func (a *Article) ToDomain() *domain.Article {
	return &a.Article
}

func FromDmToApiArticle(a *domain.Article, isBookmarked bool) *Article {
	return &Article{Article: *a, IsBookmarked: isBookmarked}
}

func (p *Publisher) ToDomain() *domain.Publisher {
	return &p.Publisher
}

func FromDmToApiPublisher(p *domain.Publisher, isSubscribed bool) *Publisher {
	return &Publisher{Publisher: *p, IsSubscribed: isSubscribed}
}
