from qdrant_client import QdrantClient, models
import json
import recommended_helper_functions
import numpy as np
qdrant = QdrantClient(
    url="https://511bd411-4d9b-433a-9a9b-e16cd30b84ff.us-east4-0.gcp.cloud.qdrant.io:6333", 
    api_key="vPFMrOT0Mbjh5UMy_HeBJPdvdqV66IUa6L9S2mUezs8C_aGX5Yk20Q",
)


file_path = 'output.json'
with open(file_path, 'r', encoding='utf-8') as file:
    data = json.load(file)

for item in data:
    index = qdrant.count(
        collection_name="news_collection",
        exact=True,
            )
    qdrant.upsert(collection_name="news_collection", points =
              [models.PointStruct(id=int(index.count)+1, payload= {"url": item["file_name"]},vector=recommended_helper_functions.vectorize(str(item["text"])),),],)

qdrant.count(
        collection_name="news_collection",
        exact=True,
            )