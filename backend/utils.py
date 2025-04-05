import os
import io 
from PIL import Image
from werkzeug.utils import secure_filename
import requests, base64
import shutil

#Create a temporary server side folder for audio file path
TEMP_UPLOAD_FOLDER = "C:/temp_uploads"
os.makedirs(TEMP_UPLOAD_FOLDER, exist_ok=True)

def allowed_file(filename, filetype = "image"):
    ALLOWED_IMAGE_EXTENSIONS = {"png","jpg"}
    ALLOWED_AUDIO_EXTENSIONS = {"mp3"} #only allow mp3
    _, ext = os.path.splitext(filename)
    ext = ext.lstrip('.').lower()
    if filetype == "image":
        return ext in ALLOWED_IMAGE_EXTENSIONS
    elif filetype == "audio":
        return ext in ALLOWED_AUDIO_EXTENSIONS
    else:
        return False


def generateTempAudioFile(b64Audio):
    audioData = base64.b64decode(b64Audio)

    fileName = "uploadedBirdAudio.mp3"

    temp_save_path = os.path.join(TEMP_UPLOAD_FOLDER, fileName)
   
    # actual saving is done to file is done in open, dont need to run .save()
    with open(temp_save_path,'wb') as f:
        f.write(audioData)
    

    return temp_save_path

  
def cleanup():
    #deletes temporary file
    try:
        # os.remove(temp_save_path) instead of removing a path remove the entire directory
        shutil.rmtree(TEMP_UPLOAD_FOLDER) #remove entire temp directory and any file stored in it
    except OSError as e:
        print(F"Error: {e.strerror}")

    
def get_new_image_base64(birdName):
    #going to use INaturalist API to retrieve bird image
    #if no image provided by INaturalist API retrieve default image

    imageFound = False
    #default imageURL hosted on postimg site
    image_url = "https://i.postimg.cc/7ZJsjfNS/pngimg-com-birds-PNG108.png"

    params = {
        "q": birdName,
        "sources": "taxa",
    }
    res = requests.get("https://api.inaturalist.org/v1/search", params=params)
    if res.ok:
        results = res.json().get("results",[]) #default to empty array if no results
        if results:
            image_url = results[0]["record"]["default_photo"]["square_url"]
            imageFound = True #found suitable image
       
    #Return base 64 encoding of image, using imageUrl 
    try:
        imageResponse = requests.get(image_url)

        #if imageUrl rejects request or for some reason there is a status other than the 200s
        imageResponse.raise_for_status()

        image_bytes = imageResponse.content
        encoded = base64.b64encode(image_bytes).decode('utf-8')
        return (encoded, imageFound)
    
    except Exception as exc:
        print(f"Error retrieving image from url: {exc}")
        return None    

def parse_birdName_string(response_string):
    weightedExpertResponse = ""

    for name in response_string.split("-"):
        weightedExpertResponse += name.capitalize() + " "

    # Remove the trailing space at the end
    return weightedExpertResponse.strip()





