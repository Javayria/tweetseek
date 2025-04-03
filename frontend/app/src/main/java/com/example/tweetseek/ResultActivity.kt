package com.example.tweetseek
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.tweetseek.databinding.ResultPageBinding


class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ResultPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve each value individually
        val birdName = intent.getStringExtra("bird_name")

        if (birdName != null) {
            // Display results
            binding.birdName.text = birdName
        } else {
            Toast.makeText(this, "Missing result data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}