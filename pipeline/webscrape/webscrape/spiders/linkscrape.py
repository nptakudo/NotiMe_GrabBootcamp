import scrapy
import sys
import time
from scrapy.linkextractors import LinkExtractor
from scrapy.crawler import CrawlerProcess
from bs4 import BeautifulSoup

# Define a Spider class for crawling the specific source URL
class SourceCrawler(scrapy.Spider):
    name = "src_crawler"
    start_urls = ['https://www.startdataengineering.com/']  # List of URLs to begin crawling from

    def parse(self, response):
        # Extract links from the page using LinkExtractor
        link_extractor = LinkExtractor(allow=response.url)
        links = link_extractor.extract_links(response)

        # Yield each found link as a Python dictionary
        for link in links:
            yield {"src": response.url, "blog": link.url}

# Define a Spider class that can be initiated with a specific URL
class InstantCrawl(scrapy.Spider):
    name = "src_crawler2"
    start_urls = []  # Start URLs is empty initially; set by __init__ if provided

    def __init__(self, url=None, *args, **kwargs):
        super().__init__(*args, **kwargs)
        if url:
            self.start_urls = [url]  # Set the starting URL if provided

    def parse(self, response):
        # Extract links from the page and make subsequent requests to parse blog content
        link_extractor = LinkExtractor(allow=response.url)
        links = link_extractor.extract_links(response)
        
        for link in links:
            yield scrapy.Request(link.url, callback=self.parse_blog)

    def parse_blog(self, response):
        # Parse the blog page to extract title, text, and post date
        soup = BeautifulSoup(response.body, 'lxml')
        title = soup.title.string
        text = soup.get_text().replace('\n', '')
        text = text.replace('\t', '')
        time = soup.select("[class*=date],[class*=time]", limit=1)
        time_text = time[0].text if time else "N/A"

        yield {"url": response.url, "title": title, "date": time_text, "content": text}


# Configure the crawling process with settings for performance and output format
process = CrawlerProcess(settings={
    'REACTOR_THREADPOOL_MAXSIZE': 20,
    'COOKIES_ENABLED': False,
    'LOG_LEVEL': "INFO",
    'CONCURRENT_REQUESTS': 300,
    'RETRY_ENABLED': True,
    'DOWNLOAD_TIMEOUT': 5,
    'FEEDS': {
        "output.json": {
            "format": "json",
            "overwrite": True
        }
    }
})

# Main execution block to start the crawler
if __name__ == '__main__':
    if len(sys.argv) < 2:
        sys.exit(1)  # Exit if no URL is provided

    url = sys.argv[1]
    start_time = time.time()
    process.crawl(InstantCrawl, url)  # Start the crawling process
    process.start()
    print(f"--- {time.time() - start_time} seconds ---")  # Print the total time taken for the crawl