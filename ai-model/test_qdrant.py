from qdrant_client import QdrantClient, models
import json
import numpy as np
qdrant = QdrantClient("http://localhost:6333")

file_path = '1k_sample_bs4_vectorized_data.json'
with open(file_path, 'r', encoding='utf-8') as file:
    data = json.load(file)
# Upsert the data
# Create the collection
qdrant.create_collection(collection_name="news_collection", vectors_config=models.VectorParams(size=768, distance=models.Distance.COSINE))
for item in data:
    qdrant.upsert(collection_name="news_collection", points =
              [models.PointStruct(id=data.index(item), payload= {"url": item["filename"]},vector=item["vec"],),],)
    
result = qdrant.search(
    collection_name="news_collection",
    query_vector=data[260]['vec'],
    with_vectors=True,
    with_payload=True,
)
print(result[1][0])