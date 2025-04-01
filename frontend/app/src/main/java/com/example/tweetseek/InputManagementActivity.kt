package com.example.tweetseek

import androidx.lifecycle.lifecycleScope
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AppCompatActivity
import com.example.tweetseek.databinding.InputManagementBinding
import com.example.tweetseek.identification.IdentificationManager
import com.example.tweetseek.identification.RequestData
import kotlinx.coroutines.launch
import java.io.IOException

// TODO clean this class up
class InputManagementActivity : AppCompatActivity() {
    private lateinit var binding: InputManagementBinding

    private var base64Image: String? = null
    private var base64Audio: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InputManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Populate dropdowns
        setupDropdowns()

        // onClicks for image/audio buttons
        binding.imgUpload.setOnClickListener { imgLauncher.launch("image/*") }
        binding.audioUpload.setOnClickListener { audioLauncher.launch("audio/*") }

        // Make POST request when submitted
        binding.submitButton.setOnClickListener {
            val requestData = RequestData(
                base64Image ?: "",
                base64Audio ?: "",
                binding.sizeDropdown.text.toString(),
                binding.colorDropdown.text.toString(),
                binding.locationDropdown.text.toString()
            )

            val idManager = IdentificationManager(requestData)

            lifecycleScope.launch {
                val response = idManager.identifyBird()
                Log.d("InputManagement", "Server response: $response")
            }
        }


    }

    // Launchers for onClicks
    private val imgLauncher = registerForActivityResult(GetContent()) { uri ->
        base64Image = uriToBase64(uri)
    }
    private val audioLauncher = registerForActivityResult(GetContent()) { uri ->
        base64Audio = uriToBase64(uri)
    }

    // Convert URI of image/audio to Base64
    private fun uriToBase64(uri: Uri?): String {
        try {
            val bytes = contentResolver.openInputStream(uri!!)?.readBytes()
            return Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (error: IOException) {
            Log.d("InputManagement", error.toString())
            return ""
        }
    }

    // Populate dropdowns with options
    private fun setupDropdowns() {
        val sizes = listOf("Small", "Medium", "Large")
        val colors = listOf("Red", "Blue", "Brown", "Black", "White")
        val locations = listOf("Forest", "Desert")

        // Populating components with arrays of options
        binding.sizeDropdown.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sizes)
        )
        binding.colorDropdown.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, colors)
        )
        binding.locationDropdown.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, locations)
        )
    }
}
