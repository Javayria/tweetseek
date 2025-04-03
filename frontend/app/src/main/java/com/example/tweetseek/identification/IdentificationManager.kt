
package com.example.tweetseek.identification

import android.content.Context
import android.util.Log
import com.example.tweetseek.identification.database.ReportDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

// TODO clean this class up
class IdentificationManager(private val request: RequestData) {
    private var currentResult: IdentificationResult? = null

    fun init(context: Context) {
        ReportDatabase.init(context)
    }

    private val JSON = "application/json".toMediaType()
    private val client = OkHttpClient()

    // Contacts forum and determines bird species
    suspend fun identifyBird(): IdentificationResult? = withContext(Dispatchers.IO) {
        currentResult = null

        // Build JSON body
        val body = JSONObject().apply {
            put("imageFile", request.imageFile)
            put("audioFile", request.audioFile)
            put("size", request.size)
            put("color", request.color)
            put("location", request.location)
        }

        // POST request to backend
        try {
            val responseJSON = get("http://10.0.2.2:8000/")
            val result = parseResponse(responseJSON)
            return@withContext result

        } catch (e: Exception) {
            Log.e("IdentificationManager", "Identification failed", e)
            null
        }
    }

    // POST request creation
    // TODO move out of here
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

    // GET request creation
    // TODO move out of here
    private suspend fun get(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Unexpected response code: ${response.code}")
        }

        val body = response.body?.string()
        if (body == null) {
            Log.e("IdentificationManager", "GET response body is null")
            throw IOException("Empty response body")
        }

        Log.d("IdentificationManager", body)
        return@withContext body
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