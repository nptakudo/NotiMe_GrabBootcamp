import scrapy 
from scrapy.linkextractors import LinkExtractor 
from scrapy.crawler import CrawlerProcess
import polars as pl

# create a data frame by reading a csv file
# df = pl.read_csv('html_urls.csv')
df = pl.read_csv('/Users/takudo/Documents/NotiMe_GrabBootcamp/data/webscrape/webscrape/spiders/test.csv')
#read half of the data frame
# first_df = df.head(df.shape[0]//2)
#read the other half of the data frame
# second_df = df.tail(df.shape[0]//2)

class SourceCrawler(scrapy.Spider): 
    name = "src_crawler"
    # read urls from the data frame
    start_urls = df['src'].to_list()
    # start_urls = ['https://www.startdataengineering.com/']

    def parse(self, response): 
        # initialize the link extractor
        link_extractor = LinkExtractor(allow=response.url) 
        links = link_extractor.extract_links(response) 

        #extract the links from the response
        for link in links:
            yield {"src":response.url,"blog": link.url}
        # #get xml file
        # with open('output.xml', 'wb') as file:
        #     file.write(response.body)
       
        

process = CrawlerProcess(
    settings={
        # 'SCHEDULER_PRIORITY_QUEUE' : "scrapy.pqueues.DownloaderAwarePriorityQueue",
        'REACTOR_THREADPOOL_MAXSIZE' : 20,
        'COOKIES_ENABLED' : False,
        'LOG_LEVEL' : "INFO",
        # 'ITEM_PIPELINES' : {
        #     "webscrape.pipelines.HTMLToS3": 300,
        #     },
        'CONCURRENT_REQUESTS' : 300,
        'RETRY_ENABLED' : True,
        'DOWNLOAD_TIMEOUT': 5,
        'FEEDS': {
            "s3://raw/source_outputs.json": {
            "format": "json"
            }},
        # 'FEED_FORMAT': 'json',
        # 'FEED_URI': 'output.json',
        'AWS_ENDPOINT_URL': 'http://localhost:9000',
        'AWS_REGION_NAME': 'us-east-1',
        'AWS_ACCESS_KEY_ID': '7Vatx4rSRWLj73ArsqAS',
        'AWS_SECRET_ACCESS_KEY': '9v1Z718T3zUvvLDV2h2xZXZwjMJaOSScM6AHOSSq'
    }
)
process.crawl(SourceCrawler)
# process.crawl(GetTitle)
process.start()
