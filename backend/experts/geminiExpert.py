
class GeminiExpert:

    def __init__(self, client):
        self.client = client 

    def analyze_image(self,image):
        response = self.client.models.generate_content(
            model="gemini-2.0-flash-001",
            #need to split with hyphen because the base64 function expects a string search param without any spaces
            contents=[image,"Identify the bird from the provided photo. If the bird has multiple words in its name (e.g., 'cedar waxwing'), separate the words using a hyphen ('-'). Only provide the bird's name, not any other details."] #hardcoded to provide gemini with the prompt for identifying the image
        )

        return response.text
    
