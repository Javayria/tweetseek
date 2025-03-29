package com.example.tweetseek

import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.content.Intent
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
    lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener(View.OnClickListener {
            if (binding.username.text.toString() == "user" && binding.password.text.toString() == "abc123"){
                Toast.makeText(this, "login success!", Toast.LENGTH_SHORT).show()

                // go to MainActivity
                val intent = Intent(this, HomePage::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, "login failed :(", Toast.LENGTH_SHORT).show()
            }
        })

        binding.registerButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        })
    }
}