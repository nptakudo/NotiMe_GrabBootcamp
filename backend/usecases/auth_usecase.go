package usecases

import (
	"context"
	"errors"
	"log/slog"
	"notime/api/controller"
	"notime/api/messages"
	"notime/api/tokenutils"
	"notime/bootstrap"
	"notime/domain"
	"notime/external/sql/store"
	"notime/repository"
)

type LoginUsecaseImpl struct {
	UserRepository domain.UserRepository
	env            *bootstrap.Env
}

func NewLoginUsecase(env *bootstrap.Env, db *store.Queries) controller.LoginUsecase {
	userRepository := repository.NewUserRepository(db)

	return &LoginUsecaseImpl{
		env:            env,
		UserRepository: userRepository,
	}
}

func (uc *LoginUsecaseImpl) Login(ctx context.Context, username string, password string) (*messages.User, error) {
	userDm, err := uc.UserRepository.GetByUsername(ctx, username)
	if err != nil {
		slog.Error("[LoginUsecase] GetUserByUsername:", "error", err)
		return nil, errors.New("username not found")
	}

	if userDm == nil || !uc.comparePasswords(userDm.Password, password) {
		slog.Error("[LoginUsecase] GetUserByUsername:", "error", "wrong password")
		return nil, errors.New("wrong password")
	}

	accessToken, err := tokenutils.CreateAccessToken(userDm, uc.env.AccessTokenSecret, uc.env.RefreshTokenExpiryHour)
	if err != nil {
		slog.Error("[LoginUsecase] CreateAccessToken:", "error", err)
		return nil, errors.New("failed to create access token")
	}

	return fromDmUserToApi(userDm, accessToken), nil
}

func (uc *LoginUsecaseImpl) comparePasswords(storedPassword, providedPassword string) bool {
	// Use a proper password hashing comparison function, e.g., bcrypt
	// Example: return bcrypt.CompareHashAndPassword([]byte(storedPassword), []byte(providedPassword)) == nil
	return storedPassword == providedPassword // Replace this with actual comparison logic
}
