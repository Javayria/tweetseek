from flask import (Flask , request, Response, stream_with_context, jsonify)

from google import genai


from dotenv import load_dotenv
from werkzeug.utils import secure_filename
from utils import allowed_file
#for image processing
from PIL import Image
import os

#for image processing
import io


#load env variables
load_dotenv()

ALLOWED_EXTENSIONS = {"png","jpg","jpeg"}

# Only run this block for Gemini Developer API
# google's gemini client, globally configure api key
client = genai.Client(api_key=os.getenv("GOOGLE_API_KEY"))

app = Flask(__name__)


form_image = ""

@app.route('/')
def home():
    return "Hello tweetseek!"

@app.route('/uploadimage',methods=['POST'])
def imageContext():
    # checks validity of uploading image file, saves it, uses it on form submission, on form cancellation the image will be cleared

    global form_image 

    if "file" not in request.files:
       return jsonify(success=False, message="No file part")

    file = request.files["file"]

    if file.filename == "":
        return jsonify(success=False, message="No selected file")
    if file and allowed_file(file.filename):
        filename = secure_filename(file.filename)

        #essentially this code is used to process an uploaded image without saving it to a disk
        # Read the file stream into a BytesIO object
        file_stream = io.BytesIO(file.read())
        #moves pointer back to start of the BytestIO stream
        file_stream.seek(0)
        #reads image data and prepares it as an image object
        form_image = Image.open(file_stream)

        return jsonify(
            success=True,
            message="File uploaded successful",
            filename=filename,
        )
    return jsonify(success=False, message="File type not allowed")


@app.route('/geminiexpert',methods=['GET'])
def geminiExpert():

    # https://ai.google.dev/gemini-api/docs/text-generation#python

    global form_image

    if form_image != "":
        
        #choose not to stream content back as styling for UI will be harder
        response = client.models.generate_content(
            model="gemini-2.0-flash-001",
            contents=[form_image,"Only provide the bird's name, identify the bird from the provided photo"] #hardcoded to provide gemini with the prompt for identifying the image
        )

        print(response)

        return jsonify(
            success=True,
            message= response.text
        )
    
    else:
        return jsonify(
            success=False,
            message="image file not provided"
        )

# @app.route('/clearform',methods=['DELETE'])

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=8000)
