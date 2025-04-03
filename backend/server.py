from flask import (Flask, jsonify)

from google import genai


from dotenv import load_dotenv
from experts.geminiExpert import GeminiExpert

from routes import register_routes
import os

#load env variables
load_dotenv()

ALLOWED_EXTENSIONS = {"png","jpg","jpeg"}

"""
Only run this block for Gemini Developer API
google's gemini client, globally configure api key
"""
# initialize experts in server.py, experts are instaniated once and injected where needed
# better for testing, you can mock experts without having to modify individual routes
client = genai.Client(api_key=os.getenv("GOOGLE_API_KEY"))
geminiExpert = GeminiExpert(client=client)

app = Flask(__name__)

#register routes and inject dependencies
register_routes(app,geminiExpert)

@app.route('/')
def home():
    return jsonify(
        birdImage = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDIBCQkJDAsMGA0NGDIhHCEyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMv/CABEIALQA8AMBIgACEQEDEQH/xAAzAAACAwEBAQAAAAAAAAAAAAAEBQIDBgABBwEAAwEBAQEAAAAAAAAAAAAAAgMEAQUABv/aAAwDAQACEAMQAAAAyNdsHjGq6kfR6XZvQujnhrr9Gl2So3SCN6ouy4J2ZANzSIujdJoFULxEYNsIIrOskN1tLaFvNQrtavp1C2IdbQg7yds3eWkLYu5iOBD2xNJVmkVO43HL2a8PLlxKxe1sFreYjRvAMAEiBfjr9tW9ROivVP8A6nlhqPoqjiUYmomn562GgStcdm4k19IYGSIEvSfTzJZU5HakNpAuNw6siOgkm9DUkc4I5XgpEFZgMWY5+XItMjpJJ9P+DfZvpoGZrLJyOlnw6o9ZlVG8Rye9WwXYbCqpYXej2e0qrodHzYkI81qWJ0AC8MvgNQ3KIUa8Y9coJmAs61BDPl31MOaVfS83hfUk7LJSsyz5E25DzkzRWK8qT7Q62r0W6HGV4zJZVW3QRrBmjlUh4vVrjHT3oD46n1K+NE1Ur7ejOQYq8+njaeUNvFVmPoWGlZqcUgM5T2ERQ+dUfUtsFVQ5F9NIHhvnvDOx35CLQxUTMuDqqj8NOwgphWIrdoE6b50m78WlTN3fVnR9njb53b7Jyko+hrRHHMu+EbcqHz1SOp0GBwPHIkUbeB70SGLqYK8TIdVWohaPYi7mJbyMEHm5XdznYDzaqfoecPUuVexvlNg/no+bOPpdklGFJ1QMzvBC1POMeu6jmMZL7xHh6aOYxSm1XdW5v0BwT4A5bU+yOtsyrE/Rc/nWntEqllzJ3Ix3I7Ou6jKM15Db5qzHWUWTiP0kSEhFXrzeYZFFg2j7avnWlvRVbo5edJPe17p8L4oTlbC7n0waR5L6IgXTqYtc/RQrSI1PANzRFZYP1n4e3Pu9RcDTKwmuqa2H3jW8vZV1WEEqW1XYnFZKi4W5acvaba2HvqyNY502IHC6IzFxhKCAlLwhwg+keLGm7iDWSA2VV0e7HHEX7o8mzAmL2U6uYEIkECVEKx6s7ugxU6/aPVn3LbiG/vYS+JrHuJIXWST62zo1elXVJifI8Kkmjjm9FZ+O2kyfjtjkTcxYn1CmJK9gpnUn0SyliiDAG055sPusazK7oEe93adB3c30S+6XYDdxHEnuYqIncC3Wg7regzq7sYp+b9wb9FX9wKTD906aqe7pTWMO6Zn",
        birdName = "blue jay",
        expert = "gemini"
    )




if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=8000)
