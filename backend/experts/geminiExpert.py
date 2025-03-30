
class GeminiExpert:
    def __init__(self, client):
        self.client = client 

    def analyze_image(self,image):
        response = self.client.models.generate_content(
            model="gemini-2.0-flash-001",
            contents=[image,"Only provide the bird's name, identify the bird from the provided photo"] #hardcoded to provide gemini with the prompt for identifying the image
        )

        return response.text