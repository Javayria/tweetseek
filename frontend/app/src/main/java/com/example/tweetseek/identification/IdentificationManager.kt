package com.example.tweetseek.identification

import android.util.Base64
import android.util.Log
import com.example.tweetseek.model.IdentificationResultData
import com.example.tweetseek.model.RequestData
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

class IdentificationManager(private val requestData: RequestData) {
    private val JSON = "application/json".toMediaType()
    private val client = OkHttpClient()
    private val storage = FirebaseStorage.getInstance("gs://tweetseek.firebasestorage.app")
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    suspend fun submitIdentificationRequest(): IdentificationResultData? = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("base64Image", requestData.imageFile)
            put("base64Audio", requestData.audioFile)
            put("size", requestData.size)
            put("color", requestData.color)
            put("location", requestData.location)
        }

        try {
          
//            //FOR NOW - RETURN A HARDCODED RESULT
//            val testResult = testResponse()
//            return@withContext testResult

            val response = post("http://10.0.2.2:8000/user/submitform", body.toString())
            Log.d("IdentificationManager", response)
            val result = parseResponse(response)
            val downloadUrl = uploadImage(result.birdImage)
            val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            val reportId = UUID.randomUUID().toString()
            val reportData = mapOf(
                "imagePath" to downloadUrl,
                "audioFile" to requestData.audioFile,
                "birdName" to result.birdName,
                "expert" to result.expert,
                "funFact" to result.funFact,
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

    private fun parseResponse(jsonString: String): IdentificationResultData {
        val json = JSONObject(jsonString)
        val formResponse = json.getJSONObject("formResponse")
        return IdentificationResultData(
            birdImage = formResponse.getString("birdImage"),
            birdName = formResponse.getString("birdName"),
            expert = formResponse.getString("expert"),
            funFact = formResponse.getString("funFact")
        )
    }

    private fun testResponse(): IdentificationResultData {
        return IdentificationResultData(
            birdImage = "data:image/jpeg;base64,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",
            birdName = "anonymous",
            expert = "gemini",
            funFact = "anonymous bird says lock in"
        )
    }
}