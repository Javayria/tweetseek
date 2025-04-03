package com.example.tweetseek
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import com.example.tweetseek.databinding.ResultPageBinding
import android.util.Base64

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ResultPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ResultPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //retrieve all data from the intent
        val birdName = intent.getStringExtra("bird_name")
        val birdImage = intent.getStringExtra("bird_image")
        val birdExpert = intent.getStringExtra("bird_expert")

        when {
            birdName == null -> {
                showErrorAndFinish("Missing bird name")
            }
            birdImage.isNullOrEmpty() -> {
                showErrorAndFinish("Missing bird image")
            }
            birdExpert == null -> {
                showErrorAndFinish("Missing expert info")
            }
            else -> {
                displayResults(birdName, birdImage, birdExpert)
            }
        }
    }

    private fun displayResults(name: String, imageBase64: String, expert: String) {
        binding.apply {

            //set text outputs
            birdName.text = name
            birdExpert.text = expert

            //decode and display the image
            try {
                val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                birdImage.setImageBitmap(decodedImage)
            } catch (e: Exception) {
                Toast.makeText(this@ResultActivity, "Error loading image", Toast.LENGTH_SHORT).show()
                birdImage.setImageResource(android.R.drawable.ic_menu_report_image)
            }

            // Optional: Add click listeners or other UI updates
        }
    }

    private fun showErrorAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }
}