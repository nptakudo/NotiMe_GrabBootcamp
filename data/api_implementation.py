import json
import requests
import numpy as np
import time
url = 'http://127.0.0.1:8000/suggest'
start_time = time.time()

respone = requests.post(url, json={'url':'https://bandcamptech.wordpress.com/2015/04/28/be-careful-how-you-rsyslog/'})

print(respone.text)
end_time=time.time()
print(end_time-start_time)