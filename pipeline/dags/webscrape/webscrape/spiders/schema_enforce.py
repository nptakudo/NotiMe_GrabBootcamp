import scrapy 
from scrapy.linkextractors import LinkExtractor 
from scrapy.crawler import CrawlerProcess
import polars as pl
from bs4 import BeautifulSoup
import io
from dataclasses import asdict
from utils.config import get_datalake_creds
import boto3

#Get minio credentials
s3_creds = get_datalake_creds()
s3 = boto3.client('s3',**asdict(s3_creds))

response = s3.get_object(Bucket='raw', Key='rm_existed.parquet')
parquet_file = io.BytesIO(response['Body'].read())
df = pl.read_parquet(parquet_file, use_pyarrow=True) 


class SilverCrawl(scrapy.Spider): 
    name = "src_crawler2"
    start_urls = df['src'].to_list()


    def parse(self, response): 
        # initialize the link extractor
        link_extractor = LinkExtractor(allow=response.url) 
        links = link_extractor.extract_links(response) 

        #extract the links from the response
        for link in links:
            yield scrapy.Request(link.url, callback=self.parse_blog)


    def parse_blog(self, response):
        #get the title of the blog
        soup = BeautifulSoup(response.body, 'lxml')
        title = soup.title.string

        #get the text of the blog
        text = soup.get_text().replace('\n','')
        yield {"url":response.url, "title":title, "text":text} 

process = CrawlerProcess(
    settings={
        # 'SCHEDULER_PRIORITY_QUEUE' : "scrapy.pqueues.DownloaderAwarePriorityQueue",
        'REACTOR_THREADPOOL_MAXSIZE' : 20,
        'COOKIES_ENABLED' : False,
        'LOG_LEVEL' : "INFO",
        'CONCURRENT_REQUESTS' : 300,
        'RETRY_ENABLED' : False,
        'DOWNLOAD_TIMEOUT': 5,
        'FEEDS': {
            "s3://silver/silver_outputs.json": {
            # "output.json": {
            "format": "json",
            "overwrite": True
            }
            },
        'AWS_ENDPOINT_URL': 'http://localhost:9000',
        'AWS_REGION_NAME': 'us-east-1',
        'AWS_ACCESS_KEY_ID': s3_creds.aws_access_key_id,
        'AWS_SECRET_ACCESS_KEY': s3_creds.aws_secret_access_key
    }
)

if __name__ == '__main__':
    process.crawl(SilverCrawl)
    process.start()
