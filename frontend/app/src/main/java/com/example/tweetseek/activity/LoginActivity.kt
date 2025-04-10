package com.example.tweetseek.activity

import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.tweetseek.databinding.LoginPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity()  {
    private lateinit var binding: LoginPageBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain Firebase instance
        auth = FirebaseAuth.getInstance()

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


        /*
            Login with X button which checks if there is a pending request
            if there is it waits otherwise it attempts to login
         */
        binding.twitterLoginButton.setOnClickListener {
            val provider = OAuthProvider.newBuilder("twitter.com")
            val pendingResultTask = auth.pendingAuthResult
            if (pendingResultTask != null) {
                pendingResultTask
                    .addOnSuccessListener {
                        handleAuthSuccess()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.localizedMessage ?: "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
            } else {
                auth.startActivityForSignInWithProvider(this, provider.build())
                    .addOnSuccessListener {
                        handleAuthSuccess()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.localizedMessage ?: "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    /**Helper function to contact Firebase Auth to get authentification result **/
    private fun authenticateUser(username:String, password:String) {
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomePageActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                    finish()
                } else {
                    val message = task.exception?.localizedMessage ?: "Authentication failed"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun handleAuthSuccess() {
        val user = auth.currentUser
        val db = FirebaseFirestore.getInstance()
        user?.uid?.let { uid ->
            val userDocRef = db.collection("Users").document(uid)
            userDocRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    val userData = hashMapOf("uid" to uid)
                    userDocRef.set(userData)
                }
            }
        }
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, HomePageActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }
}
