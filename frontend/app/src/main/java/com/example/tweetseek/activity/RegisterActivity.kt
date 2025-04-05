package com.example.tweetseek.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tweetseek.databinding.RegisterPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity()  {
    private lateinit var binding: RegisterPageBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain Firebase instance
        auth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            if(username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both a valid username and password", Toast.LENGTH_SHORT)
                    .show()
            }
            else{
                // Attempt to register user with Firebase
                auth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val db = FirebaseFirestore.getInstance()

                            // Create a new user document
                            val userData = hashMapOf(
                                "uid" to user?.uid
                            )

                            user?.uid?.let { uid ->
                                db.collection("Users").document(uid).set(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Account successfully created!", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error saving user: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            val message = task.exception?.localizedMessage ?: "Registration failed"
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
