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
