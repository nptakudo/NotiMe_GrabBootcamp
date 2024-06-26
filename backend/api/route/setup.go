package route

import (
	"github.com/gin-gonic/gin"
	"notime/api/middleware"
	"notime/bootstrap"
	"notime/external/sql/store"

	"time"
)

func Setup(env *bootstrap.Env, db *store.Queries, timeout time.Duration, gin *gin.Engine) {
	gin.Use(middleware.ResponseLoggerMiddleware())

	publicRouter := gin.Group("")
	// TODO
	NewDebugRouter(publicRouter, env, db)

	authRouter := publicRouter.Group("/auth")
	NewAuthRouter(authRouter, env, db)

	protectedRouter := gin.Group("")
	// Middleware to verify AccessToken
	protectedRouter.Use(middleware.JwtAuthMiddleware(env.AccessTokenSecret))

	homeRouter := protectedRouter.Group("/home")
	NewHomeRouter(homeRouter, env, db)

	readerRouter := protectedRouter.Group("/reader")
	NewReaderRouter(readerRouter, env, db)

	commonRouter := protectedRouter.Group("")
	NewCommonRouter(commonRouter, env, db)
}
