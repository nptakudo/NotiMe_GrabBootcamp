package middleware

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"notime/api/models"
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
					c.JSON(http.StatusUnauthorized, models.SimpleResponse{Message: err.Error()})
					c.Abort()
					return
				}
				c.Set("user-id", userID)
				c.Next()
				return
			}
			c.JSON(http.StatusUnauthorized, models.SimpleResponse{Message: err.Error()})
			c.Abort()
			return
		}
		c.JSON(http.StatusUnauthorized, models.SimpleResponse{Message: "Not authorized"})
		c.Abort()
	}
}
