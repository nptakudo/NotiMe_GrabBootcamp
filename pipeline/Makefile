docker-spin-up: 
	docker compose up --build -d

sleeper:
	sleep 15

up: docker-spin-up sleeper

down:
	docker compose down --volumes

restart: down up