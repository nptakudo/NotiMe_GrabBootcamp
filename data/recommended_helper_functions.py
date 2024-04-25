import pandas as pd
import numpy as np
from transformers import AutoTokenizer, Data2VecTextModel
import torch
from sklearn.metrics.pairwise import cosine_similarity

samples_description=['Material :PC, ABSStyles :with LanyardFeatures:Waterproof, Shockproof, Dropproof, Dustproof, Antislip, SnowProof, Antiscratch, WearresistantSize:19*10*3.5cmWeight :369gPackage Include:1 x Manual1x Waterproof Case1x LanyardCleaning Cloth1 x Oring Seal1 x Lubricant Watersealing #iphone #iphone7plus#case#phonecase#cover#phonecover#waterproof',
 'This product advantages:1, refined lines and the avantgarde design, derived from a Japanese teacher, by the precision of the nc machine tools are cut, not stamping or abrasive casting, production process is complex and rarely.2, on the surface of the anode oxidation film process, and beautiful at the same time with high corrosion and wear resistance.3, independent of the volume, mute, switch button, convenient operation.4, the bottom left hole, can install phone chain, belts, etc., has high practicability.5, the position of the contact with the shell bumper adopted polyurethane buffer, to protect your phone from damage.Six words with laser etching, shell surface, not easy to wear rub off.7, lateral flow curve fit the shape of the hand, for a long time use to reduce fatiguePackage Included:1 x Aluminum Metal Frame Case4 x Nuts1 x RJust Carabiner16 x Screws1 x Screwdriver1 x Alcohol pad1 x Bubble card1 x Manual',
 'Product description    CJ Beksul Korean Vermicelli Dang Myun Glass Noodles 500g / 100% sweet potato starch / 500g x 5pcs Bundle SET / korean food Features * Made of 100% sweet potato starch only. * It has been cut to cook conveniently How to cook 1. When the water boils, add the noodles and boil for 6 ~ 7 minutes. After then rinse them 2 ~ 3 times in cold water and raise the noodles. (After rinsing in cold water, put cooking oil on to not be swelled.) 2. If you want to make Korean style stirfried noodles, put the seasoned soy sauce on prepared noodles and ingredients(vegetables, mushrooms, meat) and stir it then fry it. Precautions * When rinsing it with cold water, please pay special attention to the risk of burns. * It would be better to eat directly after boiling otherwise it can deteriorate. * After opening, please eat at one time. If you store it for a long time, it may attract insects so please keep it sealed. Nutrient contents per 20g *The figures in parentheses are the ratio to daily nutritional value Calories: 70kcal Carbohydrates: 17g (5%) Saccharide:  Protein: 0g (0%) Fat: 0g (0%) Saturated Fat: 0g (0%) Trans Fat:  Cholesterol:  Sodium: 0mg (0%)']

tokenizer = AutoTokenizer.from_pretrained("facebook/data2vec-text-base")
model = Data2VecTextModel.from_pretrained("facebook/data2vec-text-base")
# Vetorize
def vectorize(text):
    inputs = tokenizer(text, return_tensors='pt')
    outputs = model(**inputs)
    last_hidden_states = outputs.last_hidden_state
    v = last_hidden_states.detach().numpy()
    return np.mean(v.reshape(v.shape[1], v.shape[2]), axis=0)


final_vectors = np.array([vectorize(samples_description[0]),vectorize(samples_description[1]),vectorize(samples_description[2])])
print(cosine_similarity(final_vectors, final_vectors))