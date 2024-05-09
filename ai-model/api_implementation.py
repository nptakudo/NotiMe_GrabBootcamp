import json
import requests

url = 'http://127.0.0.1:8000/text_vectorization'

with open('1kmock.json') as file:
    data = json.load(file)


respone = requests.post(url, json=data[0])

print(respone.text)