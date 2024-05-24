package repository

import (
	"context"
	"log/slog"
	"notime/domain"
	"notime/external/sql/store"
)

type UserRepositoryImpl struct {
	q *store.Queries
}

func NewUserRepository(q *store.Queries) domain.UserRepository { return &UserRepositoryImpl{q: q} }

func (r *UserRepositoryImpl) GetByUsername(ctx context.Context, username string) (*domain.User, error) {
	dbUser, err := r.q.GetUserByUsername(ctx, username)
	if err != nil {
		slog.Error("[User Repository] GetByUsername:", "error", err)
		return nil, err
	}
	dmUser, err := convertDbUserToDm(&dbUser)
	if err != nil {
		slog.Error("[User Repository] GetByUsername:", "error", err)
		return nil, err
	}
	return dmUser, nil
}
