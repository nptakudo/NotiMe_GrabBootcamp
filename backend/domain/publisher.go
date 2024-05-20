package domain

import "context"

type Publisher struct {
	Id        int32  `json:"id"`
	Name      string `json:"name"`
	Url       string `json:"url"`
	AvatarUrl string `json:"avatar_url"`
}

type PublisherRepository interface {
	GetById(ctx context.Context, id int32) (*Publisher, error)
	SearchByName(ctx context.Context, name string) ([]*Publisher, error)
	SearchByUrl(ctx context.Context, url string) (*Publisher, error)
	Create(ctx context.Context, name string, url string, avatarUrl string) (*Publisher, error)
}

type SubscribeListRepository interface {
	GetByUserId(ctx context.Context, userId int32) ([]*Publisher, error)
	IsSubscribed(ctx context.Context, publisherId int32, userId int32) (bool, error)
	AddToSubscribeList(ctx context.Context, publisherId int32, userId int32) error
	RemoveFromSubscribeList(ctx context.Context, publisherId int32, userId int32) error
}
