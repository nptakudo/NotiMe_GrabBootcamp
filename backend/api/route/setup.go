package route

import (
	"github.com/gin-gonic/gin"
	"notime/api/middleware"
	"notime/bootstrap"

	"time"
)

func Setup(env *bootstrap.Env, timeout time.Duration, gin *gin.Engine) {
	// TODO
	//publicRouter := gin.Group("")

	protectedRouter := gin.Group("")
	// Middleware to verify AccessToken
	protectedRouter.Use(middleware.JwtAuthMiddleware(env.AccessTokenSecret))

	homeRouter := protectedRouter.Group("/home")
	NewHomeRouter(homeRouter)

	readerRouter := protectedRouter.Group("/reader")
	NewReaderRouter(readerRouter)

	commonRouter := protectedRouter.Group("")
	CommonRouter(commonRouter)
}
