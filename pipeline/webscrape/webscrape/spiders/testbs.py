import requests
#import lxml
import lxml
from bs4 import BeautifulSoup
import time
start_time = time.time()
# url = 'https://medium.com/airbnb-engineering/airbnb-brandometer-powering-brand-perception-measurement-on-social-media-data-with-ai-c83019408051'
# url = "https://www.startdataengineering.com/tags/"
# response = requests.get(url)
#read html file
with open("/Users/takudo/Documents/NotiMe_GrabBootcamp/data/webscrape/webscrape/spiders/output.html") as file:
    response = file.read()
def get_title(response):
    soup = BeautifulSoup(response, 'lxml')
    title = soup.title.string
    return title

#find img
def get_img(response):
    soup = BeautifulSoup(response, 'lxml')
    img = soup.find_all('img')
    return img
print(get_title(response))
# print(get_img(response))
#get executing time

# start_time = time.time()


# # soup = BeautifulSoup(response, 'html.parser')
# soup = BeautifulSoup(response.content, 'lxml')
# title = soup.title.string
# print(title)

# #get text
# text = soup.get_text().replace('\n', ' ')
# # print(text)
# # print(soup.get_text().replace('\n', ' '))
# print("--- %s seconds ---" % (time.time() - start_time))