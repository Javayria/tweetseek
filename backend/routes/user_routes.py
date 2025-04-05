from dataclasses import asdict
from flask import (Blueprint, jsonify, request, current_app)
from models.DTOs.expertResponses import ExpertResponses
from models.DTOs.formResponseDTO import FormResponseDTO
from models.Requests.formSubmissionRequest import FormSubmissionRequest
from utils  import (cleanup, get_new_image_base64, parse_birdName_string, generateTempAudioFile)


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
    
   

    if requestData.base64Image:
       expertResponse.GeminiResponse = imageExpert.analyze_image(requestData.base64Image)
    
    
    if(requestData.base64Audio):
        audioFilePath = generateTempAudioFile(requestData.base64Audio)
        expertResponse.AudioResponse =  audioExpert.analyze_audio(audioFilePath)
        cleanup()
            

    #for now just go with gemini's response,TODO: implement weightings for expert findings, for more info on why we need to parse the response string look at geminiClass
    birdName = parse_birdName_string(expertResponse.GeminiResponse)
    
    #new form response
    formResponseDTO.birdName = birdName
    formResponseDTO.expert = "gemini"
    formResponseDTO.funFact = imageExpert.fun_fact(birdName)
    

    try:
        #incase encoding the base64 image throws an error
        base64Image, imageFound = get_new_image_base64(expertResponse.GeminiResponse)
    except Exception as ex:
        print(f"Error encoding image: {ex}")

    formResponseDTO.birdImage = base64Image
    formResponseDTO.imageFound = imageFound
    
    return jsonify(
        success = True,
        message = "Form Submit Successful",
        formResponse = asdict(formResponseDTO),
        expertResponse = expertResponse
    )
