package com.example.tweetseek
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tweetseek.account.AccountManager
import com.example.tweetseek.databinding.RegisterPageBinding

class RegisterActivity : AppCompatActivity()  {
    private lateinit var binding: RegisterPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            if(username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both a valid username and password", Toast.LENGTH_SHORT)
                    .show()
            }
            else{
                if(AccountManager.registerUser(username, password)) {
                    Toast.makeText(this, "REGISTER SUCCESSFUL!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}