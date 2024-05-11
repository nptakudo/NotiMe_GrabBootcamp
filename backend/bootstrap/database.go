package bootstrap

import (
	"context"
	"github.com/jackc/pgx/v5/pgxpool"
	"log/slog"
	"notime/external/sql/store"
	"time"
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

	pool, err := pgxpool.New(ctx, "user="+dbUser+" password="+dbPass+" host="+dbHost+" port="+dbPort+" dbname="+dbName)
	if err != nil {
		slog.Error("[Database] Unable to connect to database: %v", err)
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
