import os
import json
import boto3
import botocore
from botocore.client import Config
from dataclasses import asdict
from utils.config import get_datalake_creds
from concurrent.futures import ThreadPoolExecutor
import requests
from bs4 import BeautifulSoup
import polars as pl
import time

# Initialize S3 client with custom configuration
s3 = boto3.client(
    's3',
    **asdict(get_datalake_creds()),
    config=botocore.client.Config(max_pool_connections=10)
)

# Function to format URL into a filename-safe string
def get_html_filename(url):
    stripped_url = url.split('//')[-1]
    return stripped_url.replace('/', '').replace(':', '').replace('.', '')

# Function to read and parse HTML file from S3
def read_html(link):
    filename = get_html_filename(link)
    try:
        response = s3.get_object(Bucket='hehe', Key=f'{filename}.html')
        html = response['Body'].read().decode('utf-8')
        soup = BeautifulSoup(html, 'html.parser')
        text = soup.get_text().replace('\n', ' ')
        return {"filename": link, "text": text}
    except Exception as e:
        print(f"Error processing {link}: {e}")
        return {"filename": link, "text": "error"}

# Read URLs from a CSV file
df = pl.read_csv('/path/to/your/csv/file.csv', truncate_ragged_lines=True)
start_urls = df['src'].to_list()

# Measure execution time for processing HTML files
start = time.time()

# Process HTML files concurrently using ThreadPoolExecutor
with ThreadPoolExecutor(max_workers=10) as executor:
    results = list(executor.map(read_html, start_urls))

# Write results to JSON file
output_file = 'output_bs4gettext.json'
with open(output_file, 'w') as f:
    json.dump(results, f, indent=2)

end = time.time()
print(f"Total execution time: {end - start} seconds")
