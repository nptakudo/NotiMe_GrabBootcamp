
# performing a scrapy request to get the data from the website 
import scrapy
from scrapy.crawler import CrawlerProcess
from dataclasses import asdict
from utils.config import get_datalake_creds
import boto3
import polars as pl
df = pl.read_csv('/Users/takudo/Documents/NotiMe_GrabBootcamp/data/webscrape/webscrape/spiders/test.csv',truncate_ragged_lines=True)
s3 = boto3.client('s3',**asdict(get_datalake_creds()))
 
class MySpider(scrapy.Spider):
    name = 'scrapy_example'
    start_urls = df['src'].to_list()
     
    def parse(self, response):
        # Process the response here
 
        #write html to s3 bucket
        #split the url to get the name of the file
        url = response.url
        url = url.split('//')[-1]
        filename = url.replace('/','')
        filename = filename.replace(':','')
        filename = filename.replace('.','')
        s3.put_object(Bucket='hehe', Key=f'{filename}.html', Body=response.body)
        # with open('output.html', 'wb') as file:
        #     file.write(response.body)

process = CrawlerProcess(
    settings={
        # 'SCHEDULER_PRIORITY_QUEUE' : "scrapy.pqueues.DownloaderAwarePriorityQueue",
        # 'REACTOR_THREADPOOL_MAXSIZE' : 20,
        # 'COOKIES_ENABLED' : False,
        'LOG_LEVEL' : "INFO",
        'CONCURRENT_REQUESTS' : 300,
        'RETRY_ENABLED' : False,
        'DOWNLOAD_TIMEOUT': 2,
        # 'FEED_FORMAT': 'html',
        # 'FEED_URI': 'output2.html',
        'FEED_EXPORT_APPEND': False
    }
)
process.crawl(MySpider)
# process.crawl(CNNContentSpider)
process.start()