import os
import io 
from PIL import Image
from werkzeug.utils import secure_filename

def allowed_file(filename):
    ALLOWED_EXTENSIONS = {"png","jpg","jpeg"}
    _, ext = os.path.splitext(filename)
    return ext.lstrip('.').lower() in ALLOWED_EXTENSIONS

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