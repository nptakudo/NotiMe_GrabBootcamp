package usecases

import "errors"

var (
	ErrNotAuthorized = errors.New("not authorized")
	ErrInternal      = errors.New("internal error")
)
