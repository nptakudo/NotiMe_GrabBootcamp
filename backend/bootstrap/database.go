package bootstrap

import (
	"context"
	"github.com/jackc/pgx/v5"
	"log/slog"
	"notime/external/sql/store"
	"time"
)

type DbClient struct {
	conn *pgx.Conn
	*store.Queries
}

func (m *DbClient) Disconnect(ctx context.Context) error {
	return m.conn.Close(ctx)
}

func NewDatabase(ctx context.Context, env *Env) *DbClient {
	_, cancel := context.WithTimeout(ctx, 10*time.Second)
	defer cancel()

	dbHost := env.DBHost
	dbPort := env.DBPort
	dbUser := env.DBUser
	dbPass := env.DBPass
	dbName := env.DBName

	conn, err := pgx.Connect(ctx, "user="+dbUser+" password="+dbPass+" host="+dbHost+" port="+dbPort+" dbname="+dbName)
	if err != nil {
		slog.Error("[Database] Unable to connect to database:", err)
		panic(err)
	}
	return &DbClient{conn, store.New(conn)}
}

func CloseDbConnection(ctx context.Context, client *DbClient) {
	if client == nil {
		return
	}

	err := client.Disconnect(ctx)
	if err != nil {
		slog.Error("[Database] CloseDbConnection: %v", err)
		panic(err)
	}

	slog.Info("Connection to database closed.")
}
