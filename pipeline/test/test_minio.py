import os
import boto3
from botocore.client import Config
from dataclasses import asdict
from utils.config import get_datalake_creds


import time
import requests

s3 = boto3.client('s3',**asdict(get_datalake_creds()))
                # #   config=Config(signature_version='s3v4')

folder_path = "tmp"
bucket_name = "heheeeee"
object_prefix = "huhu-folder/"

# Create a new bucket
# s3.create_bucket(Bucket=bucket_name)

#get execution time
start = time.time()


url = 'https://2ality.com/'
start = time.time()
response = requests.get(url)
html = response.text
end = time.time()
print(end - start)

url = url.split('//')[-1]
filename = url.replace('/','')
filename = filename.replace(':','')
filename = filename.replace('.','')

# read html file
response = s3.get_object(Bucket='hehe', Key=f'{filename}.html')
html = response['Body'].read().decode('utf-8')
# print(html)
end = time.time()
print(end - start)





# s3.Bucket('huhu').upload_file('tmp/headphones-bronz.csv',"headphones-bronz.csv")
# response = s3.list_objects_v2(Bucket=bucket_name, Prefix="huhu-folder/")
# print(response)

# List all files in the folder
# for file_name in os.listdir(folder_path):
#     file_path = os.path.join(folder_path, file_name)
#     print(file_path)
#     # Upload each file to S3
#     with open(file_path, "rb") as f:
#         s3.upload_fileobj(f, bucket_name, file_name)