package com.example.tweetseek.identification
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IdentificationResult(
    val image: String,
    val birdName: String,
    val expert: String  // "image" or "audio" depending on which was used
)