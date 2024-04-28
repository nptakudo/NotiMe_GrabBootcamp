package domain

import "context"

type User struct {
	ID       uint32 `json:"id"`
	Username string `json:"username"`
	Email    string `json:"email"`
	Password string `json:"password"`
}

type UserRepository interface {
	Create(c context.Context, user *User) error
	GetByEmail(c context.Context, email string) (*User, error)
	GetByID(c context.Context, id string) (*User, error)
}
