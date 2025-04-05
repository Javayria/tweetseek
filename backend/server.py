from flask import Flask

from google import genai


from dotenv import load_dotenv
from experts.geminiExpert import GeminiExpert
from experts.audioExpert import AudioExpert
from experts.contextualExpert import ContextualExpert

from routes import register_routes
import os

#load env variables
load_dotenv()

ALLOWED_IMAGE_EXTENSIONS = {"png","jpg","jpeg"}
ALLOWED_AUDIO_EXTENSIONS = {"mp3", "wav", "ogg"}

"""
Only run this block for Gemini Developer API
google's gemini client, globally configure api key
"""
# initialize experts in server.py, experts are instaniated once and injected where needed
# better for testing, you can mock experts without having to modify individual routes
client = genai.Client(api_key=os.getenv("GOOGLE_API_KEY"))
geminiExpert = GeminiExpert(client=client)
audioExpert = AudioExpert()
contextualExpert = ContextualExpert()

app = Flask(__name__)

#register routes and inject dependencies
register_routes(app,geminiExpert,audioExpert,contextualExpert)

@app.route('/')
def home():
    return "Hello tweetseek!"

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=8000)
