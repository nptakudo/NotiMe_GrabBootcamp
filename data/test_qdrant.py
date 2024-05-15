from qdrant_client import QdrantClient, models
import json
import numpy as np
qdrant = QdrantClient(
    url="https://511bd411-4d9b-433a-9a9b-e16cd30b84ff.us-east4-0.gcp.cloud.qdrant.io:6333", 
    api_key="vPFMrOT0Mbjh5UMy_HeBJPdvdqV66IUa6L9S2mUezs8C_aGX5Yk20Q",
)

from pydantic import BaseModel
class Url(BaseModel):
    url: str
def vectorize_text(input: Url):
    vec=qdrant.scroll(
    collection_name='news_collection',
    scroll_filter=models.Filter(
        must=[
            models.FieldCondition(
                key="url",
                match=models.MatchValue(value=input),
            )
        ]
    ),
    with_vectors=True,
    )[0]
    if vec == []:
        return []
    else:
        result = qdrant.search(
        collection_name="news_collection",
        query_vector=vec[0].vector,
        with_vectors=True,
        with_payload=True,
        )
        if result[0].payload['url']==input.url:
            return [r.payload['url'] for r in result[1:6]]
        else:
            return [r.payload['url'] for r in result[0:5]]

print(vectorize_text({'url': 'https://bandcamptech.wordpress.com/2015/04/28/be-careful-how-you-rsyslog/'}))