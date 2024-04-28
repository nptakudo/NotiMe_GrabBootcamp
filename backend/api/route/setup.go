package route

import (
	"github.com/gin-gonic/gin"
	"notime/api/middleware"
	"notime/bootstrap"

	"time"
)

func Setup(env *bootstrap.Env, timeout time.Duration, gin *gin.Engine) {
	publicRouter := gin.Group("")
	// All Public APIs

	protectedRouter := gin.Group("")
	// Middleware to verify AccessToken
	protectedRouter.Use(middleware.JwtAuthMiddleware(env.AccessTokenSecret))
	// All Private APIs
	NewHomeRouter(protectedRouter)
}
