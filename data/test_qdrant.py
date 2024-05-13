from qdrant_client import QdrantClient, models
import json
import numpy as np
import recommended_helper_functions
qdrant = QdrantClient(
    url="https://511bd411-4d9b-433a-9a9b-e16cd30b84ff.us-east4-0.gcp.cloud.qdrant.io:6333", 
    api_key="vPFMrOT0Mbjh5UMy_HeBJPdvdqV66IUa6L9S2mUezs8C_aGX5Yk20Q",
)
index = qdrant.count(
        collection_name="news_collection",
        exact=True,
            )
print(int(index.count))