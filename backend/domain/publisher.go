package domain

type Publisher struct {
	ID         uint32 `json:"id"`
	Name       string `json:"name"`
	AvatarPath string `json:"avatar_path"`
}

type PublisherRepository interface {
	GetByID(id uint32) (*Publisher, error)
	GetByName(name string) (*Publisher, error)
}

type SubscribeListRepository interface {
	GetByID(id uint32) ([]*Publisher, error)
	GetByUser(userId uint32) ([]*Publisher, error)
	IsSubscribed(publisherId uint32, userId uint32) (bool, error)
	AddToSubscribeList(publisherId uint32, userId uint32) error
	RemoveFromSubscribeList(publisherId uint32, userId uint32) error
}
