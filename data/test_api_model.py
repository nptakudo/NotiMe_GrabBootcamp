from fastapi import FastAPI
from pydantic import BaseModel
import pickle
import json
import recommended_helper_functions
import uvicorn
from pyngrok import ngrok
from fastapi.middleware.cors import CORSMiddleware
import nest_asyncio

app = FastAPI()

class Input(BaseModel):
    url: str
    content: str

@app.post('/text_vectorization')
def vectorize_text(input: Input):
    text = input.content
    vec=recommended_helper_functions.vectorize(str(text))
    return vec
public_url = ngrok.connect(8000)
print('Public URL:', public_url.public_url)
nest_asyncio.apply()
uvicorn.run(app, port=8000)