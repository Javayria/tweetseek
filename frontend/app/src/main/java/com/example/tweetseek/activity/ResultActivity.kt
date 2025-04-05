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
import java.io.File
import java.io.FileOutputStream
import androidx.core.content.FileProvider

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ResultPageBinding
    private var tweetImagePath: String? = null
    private var birdName: String? = null
    private var birdExpert: String? = null

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
                displayResults(birdName!!, birdImage, birdExpert, funFact)
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

        // Upload button sends a tweet
        binding.uploadButton.setOnClickListener {
            shareTweet()
        }
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
                    saveImageToCache(decodedImage)
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

    // Save image file and store it's path
    private fun saveImageToCache(bitmap: Bitmap?) {
        if (bitmap == null) return
        try {
            val file = File(cacheDir, "shared_bird_image.jpg")
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            tweetImagePath = file.absolutePath
        } catch (e: Exception) {
            Log.e("ResultActivity", "Failed to save image to cache: ${e.message}")
        }
    }

    private fun shareTweet() {
        val name = birdName ?: return
        val expert = birdExpert ?: return
        val tweetText = "Just identified a bird using TweetSeek! \nBird: ${name.uppercase()}\nExpert: $expert\n#Birdwatching #TweetSeekApp"

        // Turn base64 image to File Object
        val file = tweetImagePath?.let { File(it) } ?: return
        // Obtain URI
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            file
        )

        // Create Intent to make tweet
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, tweetText)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setPackage("com.twitter.android")
        }

        // Attempt to tweet
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Twitter app not installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showErrorAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }
}