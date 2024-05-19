dbup:
	docker-compose -f backend/external/sql/mock_db/docker-compose.yml up
dbrm:
	docker rm -f grab_bootcamp_db && docker rm -f grab_bootcamp_adminer && docker system prune --volumes -f
scrapeserver:
	cd pipeline && python3 -m venv my_venv && source my_venv/bin/activate && pip3 install -r requirements.txt && cd webscrape/webscrape/spiders && python3 scrape_api.py
server:
	cd backend && go run notime
mockdata:
	curl -X GET 127.0.0.1:5001/debug/populate
