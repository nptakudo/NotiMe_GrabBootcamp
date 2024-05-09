import os
import json
import boto3
import botocore
from botocore.client import Config
from dataclasses import asdict
from utils.config import get_datalake_creds
import threading
from concurrent.futures import ThreadPoolExecutor

import requests
#import lxml
import lxml
from bs4 import BeautifulSoup

# url = 'https://medium.com/airbnb-engineering/airbnb-brandometer-powering-brand-perception-measurement-on-social-media-data-with-ai-c83019408051'
# url = "https://www.gojek.io/blog/courier-reimagining-how-we-send-push-notifications"
# response = requests.get(url)
#read html file
# with open("/Users/takudo/Documents/NotiMe_GrabBootcamp/data/webscrape/webscrape/spiders/output.html") as file:
#     response = file.read()
# soup = BeautifulSoup(response, 'html.parser')
# # soup = BeautifulSoup(response.content, 'lxml')
# # title = soup.title.string

# # print(title)
# text = soup.get_text()
# text = text.replace('\n', ' ')
# print(text)



import polars as pl
df = pl.read_csv('/Users/takudo/Documents/NotiMe_GrabBootcamp/data/webscrape/webscrape/spiders/test.csv',truncate_ragged_lines=True)
start_urls = df['src'].to_list()

import time
import requests


s3 = boto3.client('s3',**asdict(get_datalake_creds()), 
                  config=botocore.client.Config(max_pool_connections=10))
# read html file
def get_html_filename(url):
    url = url.split('//')[-1]
    filename = url.replace('/','')
    filename = filename.replace(':','')
    filename = filename.replace('.','')
    return filename

def read_html(link):

    filename = get_html_filename(link)
    try:
        response = s3.get_object(Bucket='hehe', Key=f'{filename}.html')
        html = response['Body'].read().decode('utf-8')
        soup = BeautifulSoup(html, 'html.parser')
        # soup = BeautifulSoup(response.content, 'lxml')
        # title = soup.title.string

        # print(title)
        text = soup.get_text()
        text = text.replace('\n', ' ')
        # print('done')
        yield {"filename":link, "text":text}
        return html
    except:
        # print('error')
        return "error"
    # thread_name = threading.current_thread().name
    # print(f"processing URL: {filename}")
    


                # #   config=Config(signature_version='s3v4')



# Create ThreadPoolExecutor
#get execution time
start = time.time()

###testing###
# url = 'https://2ality.com/'
# print(read_html(url))


with ThreadPoolExecutor(10) as pool:
     # Process JSON files concurrently using multithreading
    data = list(pool.map(read_html,start_urls))

output_file = '25k_sample_bs4gettext.json'

with open(output_file, 'w') as f:
    f.write('[\n')
    # Iterate over the generator and write each result to the file
    for results in data:
        for result in results:
            json.dump(result, f)
            f.write(',\n')
    f.write(']')
# single thread
# for url in start_urls:
#     filename = read_html(url)
            
end = time.time()
print(end - start)


# folder_path = "tmp"
# bucket_name = "heheeeee"
# object_prefix = "huhu-folder/"

# # Create a new bucket
# # s3.create_bucket(Bucket=bucket_name)

# #get execution time
# start = time.time()



# start = time.time()
# response = requests.get(url)
# html = response.text
# end = time.time()
# print(end - start)




# # print(html)
# end = time.time()
# print(end - start)





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