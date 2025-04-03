
package com.example.tweetseek.identification

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

// TODO clean this class up
class IdentificationManager(private val requestData: RequestData) {

    private val JSON = "application/json".toMediaType()
    private val client = OkHttpClient()

    // Contacts Forum and determines bird species
    fun submitIdentificationRequest() {
        // Build JSON body
        val body = JSONObject().apply {
            put("imageFile", requestData.imageFile)
            put("audioFile", requestData.audioFile)
            put("size", requestData.size)
            put("color", requestData.color)
            put("location", requestData.location)
        }

        // POST request to backend
        try {
            /*
            {
                birdImage: "aisdyuf827eb8casbd7",
                birdName: "pigeon"
                expert: "image"
            }
             */
            val response = post("http://10.0.2.2:8000/submitForm", body.toString())
            Log.d("IdentificationManager", response)
            // Get response here unpack it in proper form
            // Push image to firebase storage
            // Obtain link and push imageLink, imageName, expert to firestore
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }.toString()
    }

    fun processForumIdentificationResult() {

    }

    fun displayForumResults() {

    }

    fun generateIdentificationReport() {
    }

    fun storeIdentificationReport(): Boolean {

        return true
    }

    // POST request creation
    private fun post(url: String, json: String): String {
        val requestBody = json.toRequestBody(JSON)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Unexpected response code: ${response.code}")
        }

        val body = response.body?.string()

        if (body == null) {
            Log.e("IdentificationManager", "Response body is null")
            throw IOException("Empty response body")
        }

        Log.d("IdentificationManager", body)
        return body
    }
}