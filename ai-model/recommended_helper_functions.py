import pandas as pd
import numpy as np
from transformers import AutoTokenizer, Data2VecTextModel
import torch
from sklearn.metrics.pairwise import cosine_similarity

tokenizer = AutoTokenizer.from_pretrained("./AutoTokenizer")
model = Data2VecTextModel.from_pretrained("./Data2Vec")
# Vetorize
def vectorize(text):
    inputs = tokenizer(text, max_length = 512,return_tensors='pt')
    outputs = model(**inputs)
    last_hidden_states = outputs.last_hidden_state
    v = last_hidden_states.detach().numpy()
    return np.mean(v.reshape(v.shape[1], v.shape[2]), axis=0).tolist()

