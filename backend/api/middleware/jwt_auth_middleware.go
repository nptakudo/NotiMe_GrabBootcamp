package middleware

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"notime/api"
	"notime/api/messages"
	"notime/api/tokenutils"
	"strings"
)

func JwtAuthMiddleware(secret string) gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.Request.Header.Get("Authorization")
		t := strings.Split(authHeader, " ")
		if len(t) == 2 {
			authToken := t[1]
			authorized, err := tokenutils.IsAuthorized(authToken, secret)
			if authorized {
				userID, err := tokenutils.ExtractIDFromToken(authToken, secret)
				if err != nil {
					c.JSON(http.StatusUnauthorized, messages.SimpleResponse{Message: err.Error()})
					c.Abort()
					return
				}
				userIDUint32, err := tokenutils.HexToUint32(userID)
				if err != nil {
					c.JSON(http.StatusUnauthorized, messages.SimpleResponse{Message: err.Error()})
					c.Abort()
					return
				}
				c.Set(api.UserIDKey, userIDUint32)
				c.Next()
				return
			}
			c.JSON(http.StatusUnauthorized, messages.SimpleResponse{Message: err.Error()})
			c.Abort()
			return
		}
		c.JSON(http.StatusUnauthorized, messages.SimpleResponse{Message: "Not authorized"})
		c.Abort()
	}
}
