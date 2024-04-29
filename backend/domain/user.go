package domain

import "context"

type User struct {
	Id       uint32 `json:"id"`
	Username string `json:"username"`
	Password string `json:"password"`
}

type UserRepository interface {
	Create(c context.Context, user *User) error
	GetById(c context.Context, id string) (*User, error)
	GetByUsername(c context.Context, username string) (*User, error)
}
