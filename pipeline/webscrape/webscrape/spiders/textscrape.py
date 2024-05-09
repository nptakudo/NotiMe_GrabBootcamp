import scrapy
from scrapy.crawler import CrawlerProcess
import polars as pl
import json

# create a data frame by reading a csv file
# df = pl.read_csv('html_urls.csv')
df = pl.read_csv('test.csv')
class MySpider(scrapy.Spider):
    name = 'myspider'
    start_urls = df['src'].to_list()
    
    def parse(self, response):
        # Extracting all <a> tags with href attribute
        links = response.css('a::attr(href)').extract()

        # Filtering the links to those that start with the desired pattern
        filtered_links = [link for link in links if link.startswith(response.url)]

        # Outputting the filtered links
        for link in filtered_links:
            yield {
                'link': link
            }
class MySpider2(scrapy.Spider):
    name = 'getcontent'
    start_urls = df['src'].to_list()

    def parse(self, response):
        # Extracting the text content of the page
        content = response.body

        # Outputting the content
        with open('output.txt', 'wb') as file:
            file.write(content)

class CNNContentSpider(scrapy.Spider):
    name = 'cnn_content'
    start_urls = df['src'].to_list()  # Add any URL here, as we'll override the start request

    def parse(self, response):
        # Extracting the content using XPath
        raw_content = response.xpath('//script[contains(text(), "articleBody")]/text()').extract_first()

        # Finding the index to remove unwanted part
        start_index = raw_content.find('"articleBody":"') + len('"articleBody":"')
        end_index_to_remove = raw_content.find('",\"articleSection"')

        # Removing unwanted part from the raw content
        content = raw_content[start_index:end_index_to_remove]

        yield {
            'url': response.url,
            'content': content,

        }

process = CrawlerProcess(
    settings={
        # 'SCHEDULER_PRIORITY_QUEUE' : "scrapy.pqueues.DownloaderAwarePriorityQueue",
        # 'REACTOR_THREADPOOL_MAXSIZE' : 20,
        # 'COOKIES_ENABLED' : False,
        # 'LOG_LEVEL' : "INFO",
        'CONCURRENT_REQUESTS' : 300,
        'RETRY_ENABLED' : False,
        'DOWNLOAD_TIMEOUT': 2,
        'FEED_FORMAT': 'jsonlines',
        'FEED_URI': 'output.json',
        'FEED_EXPORT_APPEND': True
    }
)
process.crawl(MySpider)
# process.crawl(CNNContentSpider)
process.start()