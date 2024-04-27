import scrapy 
from scrapy.linkextractors import LinkExtractor 
from scrapy.crawler import CrawlerProcess
import polars as pl
# create a data frame by reading a csv file
# df = pl.read_csv('html_urls.csv')
df = pl.read_csv('test.csv')

class QuoteSpider(scrapy.Spider): 
    name = "crawler"
    # read urls from the data frame
    start_urls = df['src'].to_list()
    # start_urls = ['https://www.startdataengineering.com/']

    def parse(self, response): 
        #initialize the link extractor
        link_extractor = LinkExtractor(allow=response.url) 
        links = link_extractor.extract_links(response) 

        #extract the links from the response
        for link in links:
            yield {"source": response.url,"blog": link.url}
        
        

process = CrawlerProcess(
    settings={
        # 'SCHEDULER_PRIORITY_QUEUE' : "scrapy.pqueues.DownloaderAwarePriorityQueue",
        # 'REACTOR_THREADPOOL_MAXSIZE' : 20,
        # 'COOKIES_ENABLED' : False,
        # 'LOG_LEVEL' : "INFO",
        'CONCURRENT_REQUESTS' : 300,
        'RETRY_ENABLED' : False,
        'DOWNLOAD_TIMEOUT': 2,
        'FEED_FORMAT': 'json',
        'FEED_URI': 'output.json'
    }
)
process.crawl(QuoteSpider)
process.start()
