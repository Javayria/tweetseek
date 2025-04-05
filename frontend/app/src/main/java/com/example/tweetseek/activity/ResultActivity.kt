package com.example.tweetseek.activity

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import com.example.tweetseek.databinding.ResultPageBinding
import android.util.Base64
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide

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
        val funFact = intent.getStringExtra("funFact")

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
            funFact == null -> {
                showErrorAndFinish("Missing fun fact")
            }
            else -> {
                displayResults(birdName, birdImage, birdExpert, funFact)
            }
        }

        //exit button goes to homepage
        binding.exitButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, HomePageActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        })
    }

    private fun displayResults(name: String, image: String, expert: String, funFact: String) {
        binding.apply {
            birdName.text = "BIRD: ${name.uppercase()}"
            birdExpert.text = "identified by $expert"
            birdFunFact.text = "did you know? ${funFact.lowercase()}"

            try {
                if (image.startsWith("http")) {
                    Glide.with(this@ResultActivity)
                        .load(image)
                        .placeholder(android.R.color.darker_gray)
                        .into(birdImage)
                } else {
                    Log.d("ResultActivity: imageBase64.length ", image.length.toString())
                    val decodedImage = convertStringToBitmap(cleanBase64String(image))
                    birdImage.setImageBitmap(decodedImage)
                }

            } catch (e: Exception) {
                Toast.makeText(this@ResultActivity, "Error loading image", Toast.LENGTH_SHORT).show()
                birdImage.setImageResource(android.R.drawable.ic_menu_report_image)
            }
        }
    }

    private fun cleanBase64String(base64String: String): String {
        return if (base64String.startsWith("data:image/jpeg;base64")) {
            base64String.substringAfter(",")
        } else {
            base64String
        }
    }

    private fun convertStringToBitmap(base64Str: String): Bitmap? {
        val decodedString = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    private fun showErrorAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }
}