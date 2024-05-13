from fastapi import FastAPI
from pydantic import BaseModel
import pickle
import json
import recommended_helper_functions
import uvicorn
from pyngrok import ngrok
from fastapi.middleware.cors import CORSMiddleware
import nest_asyncio
from qdrant_client import QdrantClient, models
import numpy as np
app = FastAPI()
qdrant = QdrantClient(
    url="https://511bd411-4d9b-433a-9a9b-e16cd30b84ff.us-east4-0.gcp.cloud.qdrant.io:6333", 
    api_key="vPFMrOT0Mbjh5UMy_HeBJPdvdqV66IUa6L9S2mUezs8C_aGX5Yk20Q",
)

class Input(BaseModel):
    url: str
    content: str

@app.post('/text_vectorization')
def vectorize_text(input: Input):
    text = input.content
    vec=recommended_helper_functions.vectorize(str(text))
    result = qdrant.search(
    collection_name="news_collection",
    query_vector=np.array(vec),
    with_vectors=True,
    with_payload=True,
    )
    if result[0].payload['url']==input.url:
        return result[1].payload['url']
    else:
        index = qdrant.count(
        collection_name="news_collection",
        exact=True,
            )
        qdrant.upsert(collection_name="news_collection", points =
              [models.PointStruct(id=int(index.count)+1, payload= {"url": input.url},vector=np.array(vec),),],)
        return result[1].payload['url']
public_url = ngrok.connect(8000)
print('Public URL:', public_url.public_url)
nest_asyncio.apply()
uvicorn.run(app, port=8000)