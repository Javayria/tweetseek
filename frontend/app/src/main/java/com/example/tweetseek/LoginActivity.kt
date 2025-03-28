package com.example.tweetseek

import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.os.Bundle
import android.widget.Toast
import com.example.tweetseek.databinding.LoginPageBinding

class LoginActivity : AppCompatActivity()  {
    private lateinit var binding: LoginPageBinding

    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loginButton.setOnClickListener(View.OnClickListener {
            if (binding.username.text.toString() == "user" && binding.password.text.toString() == "abc123"){
                Toast.makeText(this, "login success!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "login failed :(", Toast.LENGTH_SHORT).show()
            }
        })
    }
}