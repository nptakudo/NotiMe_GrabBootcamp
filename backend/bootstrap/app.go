package bootstrap

import "context"

type Application struct {
	Env      *Env
	Database *DbClient
}

func App(ctx context.Context) Application {
	app := &Application{}
	app.Env = NewEnv()
	app.Database = NewDatabase(ctx, app.Env)
	return *app
}

func (app *Application) CloseDBConnection() {
	CloseDbConnection(app.Database)
}
