package controller

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"notime/bootstrap"
	"notime/external/sql/mock_db"
	"notime/external/sql/store"
)

type DebugController struct {
	Db  *store.Queries
	Env *bootstrap.Env
}

func (c *DebugController) PopulateDb(ctx *gin.Context) {
	mock_db.Populate(ctx, c.Env, c.Db)
	ctx.JSON(http.StatusOK, nil)
}
