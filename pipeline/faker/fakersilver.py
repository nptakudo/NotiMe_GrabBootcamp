from faker import Faker
# from dataclasses import asdict
# from utils.config import get_datalake_creds
import boto3
import json
from concurrent.futures import ThreadPoolExecutor
import time

# s3 = boto3.client('s3', **asdict(get_datalake_creds()))
fake = Faker()

# Record start time
start = time.time()

def generate_fake_data_batch(size):
    data = []
    for _ in range(size):
        url = fake.url()
        for _ in range(50):
            uri = url + fake.uri_path()
            title = fake.sentence(nb_words=20, variable_nb_words=True)
            text = fake.text(max_nb_chars=12000)
            data.append({"src": url, "blog": uri, "title": title, "text": text})
    return data

def write_json_file(data):
    with open('fake_data4.json', 'w') as file:
        json.dump(data, file, indent=4)

# Number of threads and data per thread
num_threads = 10
num_data_per_thread = 200

# Create ThreadPoolExecutor and gather all data in a list
all_data = []
with ThreadPoolExecutor(max_workers=num_threads) as executor:
    results = executor.map(generate_fake_data_batch, [num_data_per_thread] * num_threads)
    for result in results:
        all_data.extend(result)

# Write all collected data to a JSON file
write_json_file(all_data)

end = time.time()
print(f"Execution time: {end - start} seconds")
