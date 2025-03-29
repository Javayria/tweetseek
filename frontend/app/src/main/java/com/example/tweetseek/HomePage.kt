package com.example.tweetseek
import android.content.Intent
import android.view.View
import android.os.Bundle
import android.widget.Toast
import com.example.tweetseek.databinding.HomePageBinding

import androidx.appcompat.app.AppCompatActivity

class HomePage : AppCompatActivity() {
    private lateinit var binding: HomePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signOutButton.setOnClickListener(View.OnClickListener {
            Toast.makeText(this, "Sign Out Successful!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        })
    }
}