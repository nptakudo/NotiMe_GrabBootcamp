package bootstrap

type Application struct {
	Env      *Env
	Database *MockSQLClient
}

func App() Application {
	app := &Application{}
	app.Env = NewEnv()
	app.Database = NewDatabase(app.Env)
	return *app
}

func (app *Application) CloseDBConnection() {
	CloseDbConnection(app.Database)
}
