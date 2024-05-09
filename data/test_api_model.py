from fastapi import FastAPI
from pydantic import BaseModel
import pickle
import json
import recommended_helper_functions
import uvicorn

app = FastAPI()

class Input(BaseModel):
    url: str
    content: str

@app.post('/text_vectorization')
def vectorize_text(input: Input):
    text = input.content
    vec=recommended_helper_functions.vectorize(str(text))
    return vec

uvicorn.run(app, host="127.0.0.1", port=8000)