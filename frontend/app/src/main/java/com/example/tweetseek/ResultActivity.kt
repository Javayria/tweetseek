package com.example.tweetseek
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

        //exit button goes to homepage
        binding.exitButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, HomePage::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        })
    }

    private fun displayResults(name: String, imageBase64: String, expert: String) {
        binding.apply {
            birdName.text = "BIRD: ${name.uppercase()}"
            birdExpert.text = "identified by: $expert"

            try {
                Log.d("ResultActivity: imageBase64.length ", imageBase64.length.toString())
                val decodedImage = convertStringToBitmap(cleanBase64String(imageBase64))
                birdImage.setImageBitmap(decodedImage)

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