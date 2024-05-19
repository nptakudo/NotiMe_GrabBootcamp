package middleware

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"notime/api"
	"notime/api/messages"
	"notime/api/tokenutils"
)

func JwtAuthMiddleware(secret string) gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.Request.Header.Get("Authorization")
		if authHeader == "" {
			c.JSON(http.StatusUnauthorized, messages.SimpleResponse{Message: "Missing authorization header"})
			c.Abort()
			return
		}
		token := extractTokenFromHeader(authHeader)
		if token == "" {
			c.JSON(http.StatusUnauthorized, messages.SimpleResponse{Message: "Invalid authorization header"})
			c.Abort()
			return
		}

		authorized, err := tokenutils.IsAuthorized(token, secret)

		if authorized {
			userId, err := tokenutils.ExtractIdFromToken(token, secret)
			if err != nil {
				c.JSON(http.StatusUnauthorized, messages.SimpleResponse{Message: err.Error()})
				c.Abort()
				return
			}
			userIdInt32, err := tokenutils.HexToInt32(userId)
			if err != nil {
				c.JSON(http.StatusUnauthorized, messages.SimpleResponse{Message: err.Error()})
				c.Abort()
				return
			}
			c.Set(api.UserIdKey, userIdInt32)
			c.Next()
			return
		}
		c.JSON(http.StatusUnauthorized, messages.SimpleResponse{Message: err.Error()})
		c.Abort()
		return
	}
}

func extractTokenFromHeader(authHeader string) string {
	// Extract the token from the Authorization header
	// Example format: "Bearer <token>"
	const prefix = "Bearer "
	if len(authHeader) > len(prefix) && authHeader[:len(prefix)] == prefix {
		return authHeader[len(prefix):]
	}
	return ""
}
