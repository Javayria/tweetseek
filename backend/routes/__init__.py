from flask import Flask

from experts.geminiExpert import GeminiExpert
from experts.audioExpert import AudioExpert
from experts.contextualExpert import ContextualExpert

from .user_routes import user_bp

#register routes and inject experts into routes, experts will be initialized in server.py
def register_routes(app:Flask, geminiExpert:GeminiExpert, audioExpert:AudioExpert, contextualExpert:ContextualExpert):
    
    #make experts available to all routes by adding them to 
    app.config["IMAGE_EXPERT"] = geminiExpert
    app.config["AUDIO_EXPERT"] = audioExpert
    app.config["CONTEXTUAL_EXPERT"] = contextualExpert

    app.register_blueprint(user_bp, url_prefix='/user')

