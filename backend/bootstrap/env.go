package bootstrap

import (
	"github.com/spf13/viper"
	"log/slog"
)

type Env struct {
	AppEnv                 string `mapstructure:"APP_ENV"`
	ServerAddress          string `mapstructure:"SERVER_ADDRESS"`
	ContextTimeout         int    `mapstructure:"CONTEXT_TIMEOUT"`
	DBHost                 string `mapstructure:"DB_HOST"`
	DBPort                 string `mapstructure:"DB_PORT"`
	DBUser                 string `mapstructure:"DB_USER"`
	DBPass                 string `mapstructure:"DB_PASS"`
	DBName                 string `mapstructure:"DB_NAME"`
	AccessTokenExpiryHour  int    `mapstructure:"ACCESS_TOKEN_EXPIRY_HOUR"`
	RefreshTokenExpiryHour int    `mapstructure:"REFRESH_TOKEN_EXPIRY_HOUR"`
	AccessTokenSecret      string `mapstructure:"ACCESS_TOKEN_SECRET"`
	RefreshTokenSecret     string `mapstructure:"REFRESH_TOKEN_SECRET"`
	LlmApiKey              string `mapstructure:"LLM_API_KEY"`
}

func NewEnv() *Env {
	env := Env{}
	viper.SetConfigFile(".env")

	err := viper.ReadInConfig()
	if err != nil {
		slog.Error("[Env] Can't find the file .env: %v", err)
		panic(err)
	}

	err = viper.Unmarshal(&env)
	if err != nil {
		slog.Error("[Env] Environment cannot be loaded: %v", err)
		panic(err)
	}

	if env.AppEnv == "development" {
		slog.Info("[Env] Environment mode loaded: %s", env.AppEnv)
	}

	return &env
}
