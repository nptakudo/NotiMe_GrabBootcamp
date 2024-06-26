// TODO: Needs re-check. Source https://github.dev/amitshekhariitbhu/go-backend-clean-architecture/tree/main/internal/tokenutil
package tokenutils

import (
	"fmt"
	jwt "github.com/golang-jwt/jwt/v4"
	"notime/domain"
	"time"
)

type TokenUtil struct {
}

type JwtCustomClaims struct {
	Name string `json:"name"`
	Id   string `json:"id"`
	jwt.RegisteredClaims
}

type JwtCustomRefreshClaims struct {
	Id string `json:"id"`
	jwt.RegisteredClaims
}

func Int32ToHex(id int32) string {
	return fmt.Sprintf("%08x", id)
}

func HexToInt32(id string) (int32, error) {
	var i int32
	_, err := fmt.Sscanf(id, "%08x", &i)
	if err != nil {
		return 0, err
	}
	return i, nil
}

func CreateAccessToken(user *domain.User, secret string, expiry int) (accessToken string, err error) {
	exp := time.Now().Add(time.Hour * time.Duration(expiry))
	claims := &JwtCustomClaims{
		Name: user.Username,
		Id:   Int32ToHex(user.Id),
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: &jwt.NumericDate{Time: exp},
		},
	}
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	t, err := token.SignedString([]byte(secret))
	if err != nil {
		return "", err
	}
	return t, err
}

func CreateRefreshToken(user *domain.User, secret string, expiry int) (refreshToken string, err error) {
	claimsRefresh := &JwtCustomRefreshClaims{
		Id: Int32ToHex(user.Id),
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: &jwt.NumericDate{Time: time.Now().Add(time.Hour * time.Duration(expiry))},
		},
	}
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claimsRefresh)
	rt, err := token.SignedString([]byte(secret))
	if err != nil {
		return "", err
	}
	return rt, err
}

func IsAuthorized(requestToken string, secret string) (bool, error) {
	_, err := jwt.Parse(requestToken, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}
		return []byte(secret), nil
	})
	if err != nil {
		return false, err
	}
	return true, nil
}

func ExtractIdFromToken(requestToken string, secret string) (string, error) {
	token, err := jwt.Parse(requestToken, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}
		return []byte(secret), nil
	})

	if err != nil {
		return "", err
	}

	claims, ok := token.Claims.(jwt.MapClaims)

	if !ok && !token.Valid {
		return "", fmt.Errorf("invalid token")
	}

	return claims["id"].(string), nil
}
