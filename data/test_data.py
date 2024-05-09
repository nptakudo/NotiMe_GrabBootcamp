import json
import pandas as pd
import recommended_helper_functions
from sklearn.metrics.pairwise import cosine_similarity
file_path = '1kmock.json'
with open(file_path) as file:
    df = pd.json_normalize(json.load(file)).head(100)
df['vec']=''
for i in df.index:
    df['vec'][i]=recommended_helper_functions.vectorize(df['content'][i])

cos_sim = cosine_similarity(df['vec'].values.tolist(), df['vec'].values.tolist())[23]
cos_sim[23]=0
print(df['url'][23])
print(df['url'][recommended_helper_functions.np.argmax(cos_sim)])