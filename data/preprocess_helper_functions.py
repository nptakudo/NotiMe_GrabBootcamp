import nltk
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from nltk.stem import WordNetLemmatizer
from nltk.corpus import wordnet
from nltk.tokenize import sent_tokenize
import re
nltk.download('punkt')
nltk.download('stopwords')
nltk.download('wordnet')
nltk.download('averaged_perceptron_tagger')
nltk.download('omw-1.4')

def remove_stop_words(text):
    stop_words = set(stopwords.words('english'))
    return [t for t in text if not t in stop_words]

def normalize(text):
    text = text.lower()
    text = re.sub(r'''(?i)\b((?:https?://|www\d{0,3}[.]|[a-z0-9.\-]+[.][a-z]{2,4}/)(?:[^\s()<>]+|\(([^\s()<>]+|(\([^\s()<>]+\)))*\))+(?:\(([^\s()<>]+|(\([^\s()<>]+\)))*\)|[^\s`!()\[\]{};:'".,<>?«»“”‘’]))''', '', text, flags=re.MULTILINE)
    text = re.sub(r'(@[A-Za-z0-9]+)|([^0-9A-Za-z \t])|(\w+:\/\/\S+)', '', text)
    text = re.sub(r'\b[0-9]+\b\s*', '', text)
    text = re.sub(r"\'m", " am", text)
    text = re.sub(r"\'s", " is", text)
    text = re.sub(r"\'re", " are", text)
    text = re.sub(r"isn't", "is not", text)
    text = re.sub(r"aren't", "are not", text)
    text = re.sub(r"don't", "do not", text)
    text = re.sub(r"doesn't", "does not", text)
    text = re.sub(r"hasn't", "has not", text)
    text = re.sub(r"\'ve", " have", text)
    text = re.sub(r"haven't", "have not", text)
    text = re.sub(r"wasn't", "was not", text)
    text = re.sub(r"weren't", "were not", text)
    text = re.sub(r"didn't", "did not", text)
    text = re.sub(r"hadn't", "had not", text)
    text = re.sub(r"\'ll", " will", text)
    text = re.sub(r"won't", "will not", text)
    text = re.sub(r"can't", "can not", text)
    text = re.sub(r"mightn't", "might not", text)
    text = re.sub(r"mustn't", "must not", text)
    text = re.sub(r"needn't", "need not", text)
    text = re.sub(r"shouldn't", "should not", text)
    text = re.sub(r"couldn't", "could not", text)
    text = re.sub(r"wouldn't", "would not", text)
    text = re.sub(r"\'d", " would", text)
    return text

def get_pos(sentence):
    pos = []
    for word in sentence:
        w, p = nltk.pos_tag([word])[0]
        if p.startswith('J'):
            pos.append((w, wordnet.ADJ))
        elif p.startswith('V'):
            pos.append((w, wordnet.VERB))
        elif p.startswith('N'):
            pos.append((w, wordnet.NOUN))
        elif p.startswith('R'):
            pos.append((w, wordnet.ADV))
        else:
            pos.append(('',''))

    return pos

def lemmatizer(words):
    lemmatizer = WordNetLemmatizer()
    lemmatized_sentence = []

    for w in words:
        i,j = get_pos([w])[0]
        if j != '':
            i = lemmatizer.lemmatize(w, pos=j)
        else:
            i = lemmatizer.lemmatize(w)
        lemmatized_sentence.append(i)

    return lemmatized_sentence

def preprocess(text):
  cleaned_text = lemmatizer(remove_stop_words(word_tokenize(normalize(text))))
  words = dict()
  for word in cleaned_text:
    if word not in words:
      words[word] = 1
    else:
      words[word] += 1
  return words

txt = "Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live the blind texts. Separated they live in Bookmarksgrove right at the coast of the Semantics, a large language ocean."
print(preprocess(txt))