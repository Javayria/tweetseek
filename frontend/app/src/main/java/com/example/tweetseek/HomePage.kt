package com.example.tweetseek
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.tweetseek.databinding.HomePageBinding
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomePage : AppCompatActivity() {
    private lateinit var binding: HomePageBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain Firebase instance
        auth = FirebaseAuth.getInstance()

        binding.startIDButton.setOnClickListener {
            val intent = Intent(this, InputManagementActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }


        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, AccountSettingsActivity::class.java)
            startActivity(intent)
        }

    }

}