package main

import (
	"github.com/gin-gonic/gin"
	"log"
	"notime/api/route"
	"notime/bootstrap"
	"time"
)

func main() {
	app := bootstrap.App()
	env := app.Env
	defer app.CloseDBConnection()

	timeout := time.Duration(env.ContextTimeout) * time.Second
	gin := gin.Default()
	route.Setup(env, timeout, gin)

	err := gin.Run(env.ServerAddress)
	if err != nil {
		log.Fatalf("Failed to run server: %v", err)
	}
}
