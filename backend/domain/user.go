package domain

import "context"

type User struct {
	Id       int32  `json:"id"`
	Username string `json:"username"`
	Password string `json:"password"`
}

type UserRepository interface {
	GetByUsername(ctx context.Context, username string) (*User, error)
}
