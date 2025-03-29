package com.example.tweetseek
import android.content.Intent
import android.view.View
import android.os.Bundle
import android.widget.Toast
import com.example.tweetseek.databinding.HomePageBinding
import androidx.appcompat.app.AppCompatActivity
import com.example.tweetseek.databinding.RegisterPageBinding

class RegisterActivity : AppCompatActivity()  {
    private lateinit var binding: RegisterPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}