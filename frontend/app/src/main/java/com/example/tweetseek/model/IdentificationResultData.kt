package com.example.tweetseek.model

data class IdentificationResultData(
    val birdImage: String,
    val birdName: String,
    val expert: String  // "image" or "audio" depending on which was used
)