import os
import io 
from PIL import Image
from werkzeug.utils import secure_filename

#Create a temporary server side folder for audio file path
TEMP_UPLOAD_FOLDER = "C:/temp_uploads"
os.makedirs(TEMP_UPLOAD_FOLDER, exist_ok=True)

def allowed_file(filename, filetype = "image"):
    ALLOWED_IMAGE_EXTENSIONS = {"png","jpg","jpeg"}
    ALLOWED_AUDIO_EXTENSIONS = {"mp3", "wav", "ogg", "flac"}
    _, ext = os.path.splitext(filename)
    ext = ext.lstrip('.').lower()
    if filetype == "image":
        return ext in ALLOWED_IMAGE_EXTENSIONS
    elif filetype == "audio":
        return ext in ALLOWED_AUDIO_EXTENSIONS
    else:
        return False

def process_file(imageFile):
    #if file has dangerous characters, sanitize it 
    # filename = secure_filename(imageFile.filename)

    #essentially this code is used to process an uploaded image without saving it to a disk
    # Read the file stream into a BytesIO object
    file_stream = io.BytesIO(imageFile.read())
    #moves pointer back to start of the BytestIO stream
    file_stream.seek(0)
    #reads image data and prepares it as an image object
    form_image = Image.open(file_stream)

    return form_image

def process_audio(audioFile):
    #if file has dangerous characters, sanitize it 
    safe_filename = secure_filename(audioFile.filename)
    #create a temporary server side path for the audio file 
    temp_save_path = os.path.join(TEMP_UPLOAD_FOLDER, safe_filename)
    #writes the uploaded data to a temporary file on disk, creating a path BirdNET can use.
    audioFile.save(temp_save_path)
    return temp_save_path
    
def cleanup(temp_save_path):
    #deletes temporary file
    try:
        os.remove(temp_save_path)
    except OSError:
        pass 

    




