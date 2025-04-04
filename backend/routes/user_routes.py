from dataclasses import asdict
from flask import (Blueprint, jsonify, request, current_app)
from models.DTOs.expertResponses import ExpertResponses
from models.DTOs.formResponseDTO import FormResponseDTO
from utils  import (allowed_file, process_file,process_audio,cleanup, get_new_image_base64, parse_birdName_string)


user_bp = Blueprint('user', __name__)



"""
submit form endpoint will take a FormSubmissionRequest object and return a FormResponseObject
"""
@user_bp.route('/submitform', methods=['POST'])
def formSubmit():
    
    #experts are globally scoped within the routes module
    #access experts through app context
    imageExpert = current_app.config.get("IMAGE_EXPERT")
    audioExpert = current_app.config.get("AUDIO_EXPERT")
    #if any one of the experts are unavailable, form submission cannot be processed

    if imageExpert is None:
        return jsonify(success=False,message="Cannot process request at this time")

    if audioExpert is None:
        return jsonify(success=False,message="Cannot process request at this time")
    #initally formResponseObject will have every expert's response as null
    expertResponse = ExpertResponses()
    formResponseDTO = FormResponseDTO()

    #checks for imageFile in request files
    if "imageFile" not in request.files:
       return jsonify(success=False, message="No image file included")
    
    imageFile = request.files["imageFile"]

    if imageFile.filename == "":
        return jsonify(success=False, message="No selected image file")
    
    if imageFile and allowed_file(imageFile.filename):
        image = process_file(imageFile)
        imageAnalysisResponse = imageExpert.analyze_image(image)
    
    if(imageAnalysisResponse):
        expertResponse.GeminiResponse = imageAnalysisResponse
    
    #checks for audioFile in request files
    if "audioFile" not in request.files:
        return jsonify(success=False, message="No audio file included")

    audioFile = request.files["audioFile"]

    if audioFile.filename == "":
        return jsonify(success=False, message="No selected audio file")

    if audioFile and allowed_file(audioFile.filename, "audio"):
        audio = process_audio(audioFile)
        audioAnalysisResponse = audioExpert.analyze_audio(audio)
        cleanup(audio)
        
    if(audioAnalysisResponse):
        expertResponse.AudioResponse = audioAnalysisResponse

    #for now just go with gemini's response,TODO: implement weightings for expert findings, for more info on why we need to parse the response string look at geminiClass
    birdName = parse_birdName_string(expertResponse.GeminiResponse)

    #new form response
    formResponseDTO.birdName = birdName
    formResponseDTO.expert = "gemini"
    

    try:
        #incase encoding the base64 image throws an error
        base64Image = get_new_image_base64(expertResponse.GeminiResponse)
    except Exception as ex:
        print(f"Error encoding image: {ex}")

    formResponseDTO.birdImage = base64Image
    
    return jsonify(
        success = True,
        message = "Form Submit Successful",
        formResponse = asdict(formResponseDTO),
    )
