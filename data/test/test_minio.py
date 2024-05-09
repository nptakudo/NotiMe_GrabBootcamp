import os
import boto3
from botocore.client import Config
from dataclasses import asdict
from utils.config import get_datalake_creds

s3 = boto3.client('s3',**asdict(get_datalake_creds()))
                #   endpoint_url='http://localhost:9000',
                #   aws_access_key_id="7Vatx4rSRWLj73ArsqAS",
                #   aws_secret_access_key="9v1Z718T3zUvvLDV2h2xZXZwjMJaOSScM6AHOSSq",
                # #   config=Config(signature_version='s3v4'),
                #   region_name='us-east-1')

folder_path = "tmp"
bucket_name = "hehe"
object_prefix = "huhu-folder/"

# Create a new bucket
s3.create_bucket(Bucket=bucket_name)

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