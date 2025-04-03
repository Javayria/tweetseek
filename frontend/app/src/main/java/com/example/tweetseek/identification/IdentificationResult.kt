package com.example.tweetseek.identification

data class IdentificationResult(
    val image: String,
    val birdName: String,
    val expert: String  // "image" or "audio" depending on which was used
)