# NotiMe_GrabBootcamp
## Data
### Setup
#### 1. Virtual environment
```
cd pipeline
python3 -m venv my_venv
source my_venv/bin/activate
pip3 install -r requirements.txt
deactivate
```

#### 2. Instant scrape API usage
- Set On API
```
cd webscrape/webscrape/spiders
python3 scrape_api.py
```
- API usage
```
# bash
curl -X POST -H "Content-Type: application/json" -d '{"command": "python3 linkscrape.py https://www.startdataengineering.com/post/"}' http://127.0.0.1:5000/execute_command
```
## Backend
### Technologies

1. Go version: `1.21.6`
1. Framework: `gin-gonic`
2. Database library: `sqlc` (connect with existing db from data team)

### Steps to add new routes (home, reader, login, etc.)
1. `api/messages/`: Create new `.go` file and add message formats
2. `api/controller/`: Create new `.go` file and add (1.) use-case interface, and (2.) controller
3. `usecases/`: Create new `.go` file and add use-case implementation.
   - May have to modify repositories in `repository/` and `domain/`
   - Remember: don't return sensitive errors. May want to use `ErrInternal` or define new errors in `usecases/errors.go`.
4. `api/route/`: Create new `.go` file and add new routes (`GET`, `POST`, etc.)

### References

1. **Heavily** inspired by this repo
   about [Clean Architecture in Go](https://github.com/amitshekhariitbhu/go-backend-clean-architecture/tree/main)
2. Repo [Forum App in Go](https://github.com/victorsteven/Forum-App-Go-Backend) for API, auth, and middleware references
