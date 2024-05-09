package domain

import "context"

type User struct {
	Id       int32  `json:"id"`
	Username string `json:"username"`
	Password string `json:"password"`
}

type UserRepository interface {
	Create(ctx context.Context, c context.Context, user *User) error
	GetById(ctx context.Context, c context.Context, id string) (*User, error)
	GetByUsername(ctx context.Context, c context.Context, username string) (*User, error)
}
