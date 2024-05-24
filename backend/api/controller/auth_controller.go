package controller

import (
	"context"
	"github.com/gin-gonic/gin"
	"net/http"
	"notime/api/messages"
)

type LoginController struct {
	LoginUsecase LoginUsecase
}

type LoginUsecase interface {
	Login(ctx context.Context, username string, password string) (*messages.User, error)
}

type LoginRequest struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

func (controller *LoginController) Login(ctx *gin.Context) {
	// get username and password from body of request
	var req LoginRequest
	err := ctx.ShouldBind(&req)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, messages.SimpleResponse{Message: err.Error()})
		return
	}

	user, err := controller.LoginUsecase.Login(ctx, req.Username, req.Password)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, messages.SimpleResponse{Message: err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, messages.LoginResponse{
		Id:       user.Id,
		Username: user.Username,
		Token:    user.Token,
	})
}
