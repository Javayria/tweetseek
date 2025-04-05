package com.example.tweetseek
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.tweetseek.databinding.LoadingPageBinding
import com.example.tweetseek.identification.IdentificationManager
import com.example.tweetseek.activity.*
import com.example.tweetseek.model.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


class LoadingActivity : AppCompatActivity() {
    private lateinit var binding: LoadingPageBinding
    private val min_loading_time = 6000L // minimum time spent on loading screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoadingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // load the GIF into the imageView component
        Glide.with(this)
            .asGif()
            .load(R.drawable.loading_bird)
            .into(binding.loadingGif)

        // Cancel button click handler
        binding.cancelButton.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        val requestData = RequestData(
            imageFile = intent.getStringExtra("imageFile") ?: "",
            audioFile = intent.getStringExtra("audioFile") ?: "",
            size = intent.getStringExtra("size") ?: "",
            color = intent.getStringExtra("color") ?: "",
            location = intent.getStringExtra("location") ?: ""
        )

        lifecycleScope.launch {
            //do both the asynchronous applications in parallel
            val identification = async { fetchIdentification(requestData) }
            val delayJob = async { delay(min_loading_time) }

            //make sure both complete (so it stays on the page for AT LEAST 2 seconds
            val result = identification.await()
            delayJob.await()

            //go to the result page as soon as the response from backend is received
            result?.let {
                startActivity(Intent(this@LoadingActivity, ResultActivity::class.java).apply {
                    putExtra("bird_name", it.birdName)
                    putExtra("bird_image", it.birdImage)
                    putExtra("bird_expert", it.expert)
                })
            }
            finish()
        }
    }

    private suspend fun fetchIdentification(requestData: RequestData): IdentificationResultData? {
        return try {
            IdentificationManager(requestData).submitIdentificationRequest()
        } catch (e: Exception) {
            showToast("Error: ${e.localizedMessage}")
            null
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@LoadingActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}