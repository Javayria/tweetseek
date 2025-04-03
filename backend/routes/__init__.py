from flask import Flask

from experts.geminiExpert import GeminiExpert
from experts.audioExpert import AudioExpert
from .user_routes import user_bp

#register routes and inject experts into routes, experts will be initialized in server.py
def register_routes(app:Flask, geminiExpert:GeminiExpert, audioExpert:AudioExpert):
    
    #make experts available to all routes by adding them to 
    app.config["IMAGE_EXPERT"] = geminiExpert
    app.config["AUDIO_EXPERT"] = audioExpert

    app.register_blueprint(user_bp, url_prefix='/user')

