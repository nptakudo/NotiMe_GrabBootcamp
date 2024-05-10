import scrapy 
from scrapy.linkextractors import LinkExtractor 
from scrapy.crawler import CrawlerProcess
import polars as pl
import sys
import requests
import lxml
from bs4 import BeautifulSoup
from testbs import get_title, get_text
# create a data frame by reading a csv file
# df = pl.read_csv('html_urls.csv')
# df = pl.read_csv('/Users/takudo/Documents/NotiMe_GrabBootcamp/data/webscrape/webscrape/spiders/test.csv')
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

class InstantCrawl(scrapy.Spider): 
    name = "src_crawler2"
    start_urls = []

    def __init__(self, url=None, *args, **kwargs):
        super(InstantCrawl, self).__init__(*args, **kwargs)
        if url:
            self.start_urls = [url]

    def parse(self, response): 
        # initialize the link extractor
        link_extractor = LinkExtractor(allow=response.url) 
        links = link_extractor.extract_links(response) 

        #extract the links from the response
        for link in links:
            yield scrapy.Request(link.url, callback=self.parse_blog)
            # yield {"src":response.url,"blog": link.url, "title": get_title(response)}
        # #get xml file
        # with open('output.xml', 'wb') as file:
        #     file.write(response.body) 

    def parse_blog(self, response):
        #get the title of the blog
        soup = BeautifulSoup(response.body, 'lxml')
        title = soup.title.string
        # print(response.body)
        #get the text of the blog
        text = soup.get_text().replace('\n', ' ')
        #get the image of the blog
        # img = get_img(response)
        yield {"url":response.url, "title":title} 

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
            # "s3://raw/source_outputs.json": {
            "output.json": {
            "format": "json",
            "overwrite": True
            }
            },
        # 'FEEDS':{"overwrite": True},
        # 'FEED_FORMAT': 'json',
        # 'FEED_URI': 'output.json',
        'AWS_ENDPOINT_URL': 'http://localhost:9000',
        'AWS_REGION_NAME': 'us-east-1',
        'AWS_ACCESS_KEY_ID': '7Vatx4rSRWLj73ArsqAS',
        'AWS_SECRET_ACCESS_KEY': '9v1Z718T3zUvvLDV2h2xZXZwjMJaOSScM6AHOSSq'
    }
)
# process.crawl(SourceCrawler)
if __name__ == '__main__':
    if len(sys.argv) < 2:
        # process.crawl(GetTitle)
        # process.start()
        sys.exit(1)

    url = sys.argv[1]
    import time
    start_time = time.time()
    process.crawl(InstantCrawl,url)
    process.start()
    
    print("--- %s seconds ---" % (time.time() - start_time))
