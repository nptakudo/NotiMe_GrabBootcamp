# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html


# useful for handling different item types with a single interface
from itemadapter import ItemAdapter


class HTMLToS3:
    def process_item(self, item, spider):
        # write to s3

        # s3.put_object(Bucket='huhu', Key='test.html', Body=item['content'])
        print('hehehehehe')
        return item
