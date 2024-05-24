import os
import boto3
from botocore.client import Config
from dataclasses import asdict
from utils.config import get_datalake_creds
import time
import requests

# Initialize S3 client
s3 = boto3.client('s3', **asdict(get_datalake_creds()))

# Set folder path, bucket name, and object prefix
folder_path = "tmp"
bucket_name = "heheeeee"
object_prefix = "huhu-folder/"
filename = "headphones-bronze"

# Read HTML file from S3
response = s3.get_object(Bucket='hehe', Key=f'{filename}.html')
html = response['Body'].read().decode('utf-8')
print(html)

# Example: Uploading a file to S3 (commented out)
s3.upload_file('tmp/headphones-bronz.csv', bucket_name, 'headphones-bronz.csv')

# Example: Listing objects in S3 bucket (commented out)
response = s3.list_objects_v2(Bucket=bucket_name, Prefix=object_prefix)
print(response)

# Example: Uploading all files from local folder to S3 (commented out)
for file_name in os.listdir(folder_path):
    file_path = os.path.join(folder_path, file_name)
    print(file_path)
    with open(file_path, "rb") as f:
        s3.upload_fileobj(f, bucket_name, file_name)
