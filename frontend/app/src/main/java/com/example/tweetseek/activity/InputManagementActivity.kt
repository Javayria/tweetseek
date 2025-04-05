package com.example.tweetseek.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.tweetseek.LoadingActivity
import com.example.tweetseek.R
import com.example.tweetseek.databinding.InputManagementBinding
import com.example.tweetseek.identification.*
import com.example.tweetseek.model.RequestData
import com.google.android.material.button.MaterialButton
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.launch
import java.io.IOException


class InputManagementActivity : AppCompatActivity() {

    private lateinit var binding: InputManagementBinding
    private var base64Image: String? = null
    private var base64Audio: String? = null

    //launchers
    private val imgLauncher = registerForActivityResult(GetContent()) { uri ->
        if (uri != null) {
            base64Image = uriToBase64(uri)
            // update button appearance after upload
            binding.imgUpload.apply {
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.yellow))
                text = "Image Uploaded"
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        } else {
            binding.imgUpload.apply {
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.white))
                text = getString(R.string.upload_image)
                setTextColor(ContextCompat.getColor(context, R.color.blue))
            }
        }
    }

    private val audioLauncher = registerForActivityResult(GetContent()) { uri ->
        if (uri != null) {
            base64Audio = uriToBase64(uri)
        } else {
            Log.d("AudioLauncher", "No audio selected or conversion failed.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize bindings
        binding = InputManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setup UI elements
        setupDropdowns()
        setupClickListeners()
        checkSubmitConditions()
    }

    private fun setupDropdowns() {
        val sizes = listOf("Small", "Medium", "Large")
        val colors = listOf("Red", "Blue", "Brown", "Black", "White")
        val locations = listOf("Forest", "Wetlands", "Meadow", "Urban", "Desert")

        binding.sizeDropdown.setAdapter(createAdapter(sizes))
        binding.colorDropdown.setAdapter(createAdapter(colors))
        binding.locationDropdown.setAdapter(createAdapter(locations))
    }

    private fun createAdapter(items: List<String>) =
        ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)

    private fun setupClickListeners() {
        binding.apply {
            imgUpload.setOnClickListener { imgLauncher.launch("image/*") }
            audioUpload.setOnClickListener { audioLauncher.launch("audio/*") }
            submitButton.setOnClickListener { handleSubmit() }
            buttonBack.setOnClickListener { onBackPressedDispatcher.onBackPressed()}
        }
    }

    private fun checkSubmitConditions(): Boolean {
        val containsImage = !base64Image.isNullOrEmpty()
        val containsAudio = !base64Audio.isNullOrEmpty()

        val sizeSelected = binding.sizeDropdown.text.isNotEmpty()
        val colorSelected = binding.colorDropdown.text.isNotEmpty()
        val locationSelected = binding.locationDropdown.text.isNotEmpty()


        val partialSurvey = sizeSelected || colorSelected || locationSelected
        val completeSurvey = sizeSelected && colorSelected && locationSelected

        val canSubmit = (containsImage || containsAudio || completeSurvey) && (!partialSurvey || completeSurvey)
        return canSubmit
    }

    private fun handleSubmit() {

        if (!checkSubmitConditions()) {
            val hasPartialSurvey = binding.sizeDropdown.text.isNotEmpty() ||
                    binding.colorDropdown.text.isNotEmpty() ||
                    binding.locationDropdown.text.isNotEmpty()

            if (hasPartialSurvey) {
                Toast.makeText(
                    this,
                    "Please provide inputs for all survey fields",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Please provide at least one input (image, audio, or complete survey) to identify the bird",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }

        val requestData = RequestData(
            base64Image ?: "",
            base64Audio ?: "",
            binding.sizeDropdown.text.toString(),
            binding.colorDropdown.text.toString(),
            binding.locationDropdown.text.toString()
        )

        // launch LoadingActivity with request data
        startActivity(Intent(this, LoadingActivity::class.java).apply {
            putExtra("imageFile", requestData.imageFile)
            putExtra("audioFile", requestData.audioFile)
            putExtra("size", requestData.size)
            putExtra("color", requestData.color)
            putExtra("location", requestData.location)
        })
    }

    private fun uriToBase64(uri: Uri?): String {
        return try {
            val bytes = contentResolver.openInputStream(uri!!)?.readBytes()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: IOException) {
            Log.e("InputManagement", "Media conversion failed", e)
            ""
        }
    }
}