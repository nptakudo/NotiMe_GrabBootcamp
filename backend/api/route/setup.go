package route

import (
	"github.com/gin-gonic/gin"
	"notime/api/middleware"
	"notime/bootstrap"
	"notime/external/sql/store"

	"time"
)

func Setup(env *bootstrap.Env, db *store.Queries, timeout time.Duration, gin *gin.Engine) {
	// TODO
	//publicRouter := gin.Group("")

	protectedRouter := gin.Group("")
	// Middleware to verify AccessToken
	protectedRouter.Use(middleware.JwtAuthMiddleware(env.AccessTokenSecret))

	homeRouter := protectedRouter.Group("/home")
	NewHomeRouter(homeRouter, db)

	readerRouter := protectedRouter.Group("/reader")
	NewReaderRouter(readerRouter, env, db)

	commonRouter := protectedRouter.Group("")
	CommonRouter(commonRouter, db)
}
