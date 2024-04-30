package bootstrap

import (
	"context"
	"fmt"
	"log/slog"
	"time"
)

// MockSQLClient TODO
type MockSQLClient struct{}

func (m *MockSQLClient) Disconnect(ctx context.Context) error {
	return nil
}

func NewDatabase(env *Env) *MockSQLClient {
	_, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	dbHost := env.DBHost
	dbPort := env.DBPort
	dbUser := env.DBUser
	dbPass := env.DBPass

	_ = fmt.Sprintf("db://%s:%s@%s:%s", dbUser, dbPass, dbHost, dbPort)

	// Connect to database
	return &MockSQLClient{}
}

func CloseDbConnection(client *MockSQLClient) {
	if client == nil {
		return
	}

	err := client.Disconnect(context.TODO())
	if err != nil {
		slog.Error("[Database] CloseDbConnection: %v", err)
		panic(err)
	}

	slog.Info("Connection to database closed.")
}
