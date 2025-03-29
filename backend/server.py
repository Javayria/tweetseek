from flask import (Flask , request, Response, stream_with_context, jsonify)

from google import genai


from dotenv import load_dotenv

from typing import Optional
from dataclasses import (dataclass, asdict, field)
from experts.geminiExpert import GeminiExpert
from utils import (allowed_file, process_file)
import os

#load env variables
load_dotenv()

ALLOWED_EXTENSIONS = {"png","jpg","jpeg"}

# Only run this block for Gemini Developer API
# google's gemini client, globally configure api key
client = genai.Client(api_key=os.getenv("GOOGLE_API_KEY"))
imageExpert = GeminiExpert(client=client)

app = Flask(__name__)


@app.route('/')
def home():
    return "Hello tweetseek!"


"""
submit form endpoint will take a FormSubmissionRequest object and return a FormResponseObject
"""

@dataclass 
class FormResponseObject:
    GeminiResponse : Optional[str] = field(default = None)
    AudioResponse: Optional[str] = field(default = None)
    ContextualResponse: Optional[str] = field(default = None)

@app.route('/submitform',methods=['POST'])
def formSubmit():
    #initally formResponseObject will have every expert's response as null
    formResponse = FormResponseObject()

    #checks for imageFile in request files
    if "imageFile" not in request.files:
       return jsonify(success=False, message="No image file included")
    
    imageFile = request.files["imageFile"]

    if imageFile.filename == "":
        return jsonify(success=False, message="No selected image file")
    
    if imageFile and allowed_file(imageFile.filename):
        image = process_file(imageFile)
        imageExpertResponse = imageExpert.analyze_image(image)
    
    if(imageExpertResponse):
        formResponse.GeminiResponse = imageExpertResponse

    return jsonify(
        success = True,
        message = "Form Submit Successful",
        formResponse = asdict(formResponse)
    )


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=8000)
