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
	WebscrapeHost          string `mapstructure:"WEBSCRAPE_HOST"`
	WebscrapePort          string `mapstructure:"WEBSCRAPE_PORT"`
	PElementThreshold      int    `mapstructure:"P_ELEMENT_THRESHOLD"`
	PElementCharCount      int    `mapstructure:"P_ELEMENT_CHAR_COUNT"`
	RecsysHost             string `mapstructure:"RECSYS_HOST"`
	RecsysPort             string `mapstructure:"RECSYS_PORT"`
}

func NewEnv() *Env {
	env := Env{}
	viper.SetConfigFile(".env")

	err := viper.ReadInConfig()
	if err != nil {
		slog.Error("[Env] Can't find the file .env:", "error", err)
		panic(err)
	}

	err = viper.Unmarshal(&env)
	if err != nil {
		slog.Error("[Env] Environment cannot be loaded:", "error", err)
		panic(err)
	}

	if env.AppEnv == "development" {
		slog.Info("[Env] Environment mode loaded:", "env", env.AppEnv)
	}

	return &env
}
