package domain

type Publisher struct {
	Id         uint32 `json:"id"`
	Name       string `json:"name"`
	Url        string `json:"url"`
	AvatarPath string `json:"avatar_path"`
}

type PublisherRepository interface {
	GetById(id uint32) (*Publisher, error)
	GetByName(name string) (*Publisher, error)
}

type SubscribeListRepository interface {
	GetById(id uint32) ([]*Publisher, error)
	GetByUser(userId uint32) ([]*Publisher, error)
	IsSubscribed(publisherId uint32, userId uint32) (bool, error)
	AddToSubscribeList(publisherId uint32, userId uint32) error
	RemoveFromSubscribeList(publisherId uint32, userId uint32) error
}
