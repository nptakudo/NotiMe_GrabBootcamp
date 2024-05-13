import json
import requests
import numpy as np
import time
url = 'https://5927-115-73-212-141.ngrok-free.app/text_vectorization'
start_time = time.time()
with open('1kmock.json') as file:
    data = json.load(file)

respone = requests.post(url, json=data[0])

print(respone.text)
end_time=time.time()
print(end_time-start_time)