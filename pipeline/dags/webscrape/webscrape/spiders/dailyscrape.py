import scrapy 
from scrapy.linkextractors import LinkExtractor 
from scrapy.crawler import CrawlerProcess
import polars as pl
import lxml
from bs4 import BeautifulSoup
from sqlalchemy import create_engine
from utils.config import get_warehouse_creds, get_datalake_creds

# Get MinIO credentials
s3_creds = get_datalake_creds()
# Get database credentials
db_creds = get_warehouse_creds()

# Construct the connection URL
connection_url = f"postgresql://{db_creds.user}:{db_creds.password}@{db_creds.host}:{db_creds.port}/{db_creds.db}"
engine = create_engine(connection_url)

# Use polars to read data from SQL
df = pl.read_sql("SELECT url FROM source", con=engine)

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


class DailyCrawl(scrapy.Spider):
    name = "instant_crawl"
    start_urls = df['src'].to_list()  # Read URLs from the dataframe

    def parse(self, response):
        # Initialize the link extractor with same-domain links
        link_extractor = LinkExtractor(allow=response.url) 
        links = link_extractor.extract_links(response)

        # Extract links and initiate a request for each link
        for link in links:
            yield scrapy.Request(url=link.url, callback=self.parse_blog, meta={'src': response.url})

    def parse_blog(self, response):
        # Parse the blog page using BeautifulSoup
        soup = BeautifulSoup(response.body, 'html.parser')
        title = soup.title.string if soup.title else 'No Title Found'

        # Extract text content
        text = soup.get_text().replace('\n', '') if soup else 'No Text Found'


        # Yield results including the source URL
        yield {
            "src": response.meta['src'],
            "url": response.url,
            "title": title,
            "text": text
        }

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
            "s3://silver/source_outputs.json": {
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
    process.crawl(DailyCrawl)
    process.start()