db:
	docker-compose -f external/sql/mock_db/docker-compose.yml up
server:
	go run notime
sqlcgen:
	docker run --rm -v $(PWD):/src -w /src kjconroy/sqlc generate