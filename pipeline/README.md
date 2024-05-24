# NotiMe_GrabBootcamp
## Overall pipeline
![alt text](/images/data_pipeline.png)

## Setup 
### 1. Docker environment
```bash
make docker-spin-up
```
### 2. Virtual environment
```bash
cd pipeline
python3 -m venv my_venv
source my_venv/bin/activate
pip3 install -r requirements.txt
```
- **ONLY IF** you want to ***end*** the venv : 
```bash
# deactivate
```
### 3. Jump into spark cluster container

### 4. Instant scrape API usage
- Turn On API
```bash
cd webscrape/webscrape/spiders
python3 scrape_api.py
```
- API usage
```bash
curl -X POST -H "Content-Type: application/json" -d '{"command": "python3 linkscrape.py https://www.startdataengineering.com/post/"}' http://127.0.0.1:5000/execute_command
```