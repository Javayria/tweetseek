package com.example.tweetseek

import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.tweetseek.account.AccountManager
import com.example.tweetseek.databinding.LoginPageBinding

class LoginActivity : AppCompatActivity()  {
    private lateinit var binding: LoginPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //login button is clicked -> performs user authentification
        binding.loginButton.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            if(username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT)
                    .show()
            }
            else{
                authenticateUser(username, password)
            }
        }

        //register button is clicked -> brings to Register Account page
        binding.registerButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        })
    }

    /**Helper function to ask AccountManager to get authentication result **/
    private fun authenticateUser(username:String, password:String) {

        val authResult = AccountManager.checkCredentials(username, password)

        /**THIS IS PURELY FOR TESTING PURPOSED TO GO TO HOME PAGE **/
        if ((username == "user" && password == "abc123") || (authResult == AccountManager.AuthResult.SUCCESS)) {
            Toast.makeText(this, "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomePage::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            finish()
        }
        else if (authResult == AccountManager.AuthResult.USER_NOT_FOUND) {
            Toast.makeText(this, "Username does not exist - please register an account.", Toast.LENGTH_SHORT).show()
        }
        else if (authResult == AccountManager.AuthResult.INVALID_PASSWORD) {
            Toast.makeText(this, "Incorrect password - please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}