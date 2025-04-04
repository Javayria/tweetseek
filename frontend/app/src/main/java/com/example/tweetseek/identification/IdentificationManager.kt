package com.example.tweetseek.identification

import android.util.Base64
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.UUID

class IdentificationManager(private val request: RequestData) {
    private val JSON = "application/json".toMediaType()
    private val client = OkHttpClient()
    private val storage = FirebaseStorage.getInstance("gs://tweetseek.firebasestorage.app")
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    suspend fun submitIdentificationRequest(): IdentificationResult? = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("imageFile", request.imageFile)
            put("audioFile", request.audioFile)
            put("size", request.size)
            put("color", request.color)
            put("location", request.location)
        }

        try {
            val responseJSON = post("http://10.0.2.2:8000/", body.toString())
            val result = parseResponse(responseJSON)

            // Upload image to Firebase Storage which returns download url
            val downloadUrl = uploadImage(result.birdImage)
            val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            val reportId = UUID.randomUUID().toString()
            val reportData = mapOf(
                "imagePath" to downloadUrl,
                "birdName" to result.birdName,
                "expert" to result.expert
            )

            firestore.collection("Users")
                .document(uid)
                .collection("reports")
                .document(reportId)
                .set(reportData)
                .await()

            return@withContext result.copy(birdImage = downloadUrl)

        } catch (e: Exception) {
            Log.e("IdentificationManager", "Identification failed", e)
            null
        }
    }

    private suspend fun uploadImage(base64Image: String): String {
        // Decode base64 image
        val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
        // Upload image to Firebase storage
        val imageRef = storage.reference.child("images/${UUID.randomUUID()}.png")
        imageRef.putStream(ByteArrayInputStream(imageBytes)).await()
        // Return download URL
        val downloadUrl = imageRef.downloadUrl.await().toString()
        return downloadUrl
    }

    private fun post(url: String, json: String): String {
        val requestBody = json.toRequestBody(JSON)
        val request = Request.Builder().url(url).post(requestBody).build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw IOException("Unexpected response code: ${response.code}")
        return response.body?.string() ?: throw IOException("Empty response body")
    }

    private suspend fun get(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).get().build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw IOException("Unexpected response code: ${response.code}")
        return@withContext response.body?.string() ?: throw IOException("Empty response body")
    }

    private fun parseResponse(jsonString: String): IdentificationResult {
        val json = JSONObject(jsonString)
        return IdentificationResult(
            birdImage = json.getString("birdImage"),
            birdName = json.getString("birdName"),
            expert = json.getString("expert")
        )
    }
}