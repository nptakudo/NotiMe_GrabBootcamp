package route

import (
	"github.com/gin-gonic/gin"
	"notime/api/controller"
	"notime/bootstrap"
	"notime/external/sql/store"
)

func NewDebugRouter(group *gin.RouterGroup, env *bootstrap.Env, db *store.Queries) {
	debugController := controller.DebugController{
		Env: env,
		Db:  db,
	}
	group.GET("/debug/populate", debugController.PopulateDb)
}
