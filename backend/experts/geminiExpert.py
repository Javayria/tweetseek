import base64
from google.genai import types



class GeminiExpert:

    def __init__(self, client):
        self.client = client 

    def analyze_image(self,b64Image):
        image_bytes = base64.b64decode(b64Image)
        response = self.client.models.generate_content(
            model="gemini-2.0-flash-001",
            #need to split with hyphen because the base64 function expects a string search param without any spaces
            contents=["Identify the bird from the provided photo. If the bird has multiple words in its name (e.g., 'cedar waxwing'), separate the words using a space. Only provide the bird's name, not any other details.",types.Part.from_bytes(data=image_bytes, mime_type="image/jpg")] #hardcoded to provide gemini with the prompt for identifying the image
        )

        return response.text
    
    def fun_fact(self,birdName):
        response = self.client.models.generate_content(
            model="gemini-2.0-flash-001",
            contents=[f"Generate a 1 sentence long fun fact of the bird: {birdName}"]
        )

        return response.text