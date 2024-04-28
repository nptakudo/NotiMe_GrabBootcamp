package domain

type SubscribeList struct {
	ID         uint32       `json:"id"`
	Publishers []*Publisher `json:"publishers"`
	User       *User        `json:"user"`
}

type SubscribeListRepository interface {
	GetByID(id uint32) (*SubscribeList, error)
	GetByUser(userId uint32) (*SubscribeList, error)
	IsSubscribed(publisher *Publisher, userId uint32) (bool, error)
	AddToSubscribeList(publisher *Publisher, userId uint32) error
	RemoveFromSubscribeList(publisher *Publisher, userId uint32) error
}
