docker-spin-up: 
	docker compose up --build -d --scale spark-worker=2

sleeper:
	sleep 15

up: docker-spin-up sleeper

down:
	docker compose down --volumes

restart: down up