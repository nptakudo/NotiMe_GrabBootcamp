package bootstrap

import (
	"context"
	"fmt"
	"log/slog"
	"notime/external/sql/store"
	"time"

	"github.com/jackc/pgx/v4/pgxpool"
)

type DbClient struct {
	pool *pgxpool.Pool
	*store.Queries
}

func (m *DbClient) Disconnect() {
	m.pool.Close()
}

func NewDatabase(ctx context.Context, env *Env) *DbClient {
	_, cancel := context.WithTimeout(ctx, 10*time.Second)
	defer cancel()

	dbHost := env.DBHost
	dbPort := env.DBPort
	dbUser := env.DBUser
	dbPass := env.DBPass
	dbName := env.DBName
	sslMode := "require"
	if env.AppEnv == "development" {
		sslMode = "disable"
	}

	dsn := fmt.Sprintf("host=%s user=%s password=%s dbname=%s port=%s sslmode=%s", dbHost, dbUser, dbPass, dbName, dbPort, sslMode)
	pool, err := pgxpool.Connect(ctx, dsn)
	if err != nil {
		slog.Error("[Database] Unable to connect to database:", "error", err)
		panic(err)
	}
	return &DbClient{pool, store.New(pool)}
}

func CloseDbConnection(client *DbClient) {
	if client == nil {
		return
	}
	client.Disconnect()
	slog.Info("Connection to database closed.")
}
