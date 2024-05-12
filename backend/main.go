package main

import (
	"context"
	"github.com/gin-gonic/gin"
	"log/slog"
	"notime/api/route"
	"notime/bootstrap"
	"time"
)

func main() {
	ctx := context.Background()

	app := bootstrap.App(ctx)
	env := app.Env
	defer app.CloseDBConnection()

	timeout := time.Duration(env.ContextTimeout) * time.Second
	gin := gin.Default()
	route.Setup(env, app.Database.Queries, timeout, gin)

	err := gin.Run(env.ServerAddress)
	if err != nil {
		slog.Error("Failed to run server:", "error", err)
	}
}
