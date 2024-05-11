import json
import requests
import numpy as np
url = ' https://6981-113-172-78-151.ngrok-free.app/text_vectorization'

with open('1kmock.json') as file:
    data = json.load(file)

respone = requests.post(url, json=data[0])

print(np.array(respone.text))