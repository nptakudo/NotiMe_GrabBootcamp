package route

import (
	"github.com/gin-gonic/gin"
	"notime/api/controller"
	"notime/bootstrap"
	"notime/external/sql/store"
	"notime/usecases"
)

func NewAuthRouter(group *gin.RouterGroup, env *bootstrap.Env, db *store.Queries) {
	loginController := controller.LoginController{
		LoginUsecase: usecases.NewLoginUsecase(env, db),
	}
	group.POST("/login", loginController.Login)
}
