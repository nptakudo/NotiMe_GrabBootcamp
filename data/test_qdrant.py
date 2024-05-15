from qdrant_client import QdrantClient, models
import json
import numpy as np
qdrant = QdrantClient(
    url="https://511bd411-4d9b-433a-9a9b-e16cd30b84ff.us-east4-0.gcp.cloud.qdrant.io:6333", 
    api_key="vPFMrOT0Mbjh5UMy_HeBJPdvdqV66IUa6L9S2mUezs8C_aGX5Yk20Q",
)
output = qdrant.scroll(
    collection_name='news_collection',
    scroll_filter=models.Filter(
        must=[
            models.FieldCondition(
                key="url",
                match=models.MatchValue(value="123456"),
            )
        ]
    ),
    with_vectors=True,
)


print(output)