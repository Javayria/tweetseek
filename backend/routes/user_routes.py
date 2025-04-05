from dataclasses import asdict
from flask import (Blueprint, jsonify, request, current_app)
from models.DTOs.expertResponses import ExpertResponses
from models.DTOs.formResponseDTO import FormResponseDTO
from models.Requests.formSubmissionRequest import FormSubmissionRequest
from utils  import (process_audio, cleanup, get_new_image_base64, parse_birdName_string, generateTempImageFile,generateTempAudioFile)


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
 
    #formRequest will have type formSubmissionRequest

    data = request.get_json()

    requestData = FormSubmissionRequest(**data) #unpacks a dictionary into keyword arguments

    #initally formResponseObject will have every expert's response as null
    expertResponse = ExpertResponses()
    formResponseDTO = FormResponseDTO()
    
    #need to call write to temp image file
    # image = generateTempImageFile(requestData.base64Image)
    
    imageAnalysisResponse = imageExpert.analyze_image(requestData.base64Image)
    
    if(imageAnalysisResponse):
        expertResponse.GeminiResponse = imageAnalysisResponse
    
    #checks for audioFile in request files

    audioFilePath = generateTempAudioFile(requestData.base64Audio)
    audioAnalysisResponse = audioExpert.analyze_audio(audioFilePath)
            
    if(audioAnalysisResponse):
        expertResponse.AudioResponse = audioAnalysisResponse
    
    cleanup() #remove temp_directory

    #for now just go with gemini's response,TODO: implement weightings for expert findings, for more info on why we need to parse the response string look at geminiClass
    birdName = parse_birdName_string(expertResponse.GeminiResponse)
    
    #new form response
    formResponseDTO.birdName = birdName
    formResponseDTO.expert = "gemini"
    formResponseDTO.funFact = imageExpert.fun_fact(birdName)
    

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
        expertResponse = expertResponse
    )
