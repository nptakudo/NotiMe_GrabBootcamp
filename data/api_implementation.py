import json
import requests
import numpy as np
import time
url = 'https://36a5-2401-d800-5b19-49af-341e-839d-6652-ab6f.ngrok-free.app/suggest_on_url'
start_time = time.time()

respone = requests.post(url, json={'url':'https://bandcamptech.wordpress.com/2015/04/28/be-careful-how-you-rsyslog/'})

print(respone.text)
end_time=time.time()
print(end_time-start_time)