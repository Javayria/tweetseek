package com.example.tweetseek

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tweetseek.databinding.AccountSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: AccountSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        val emailText: TextView = binding.emailText
        val newPasswordEditText: EditText = binding.newPasswordEditText

        emailText.text = user?.email ?: "No email"

        binding.updatePasswordButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString()
            if (newPassword != "") {
                user?.updatePassword(newPassword)?.addOnCompleteListener {
                    if (it.isSuccessful) Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(this, "Failed: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Enter a new password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.deleteAccountButton.setOnClickListener {
            user?.delete()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //sign out goes to the login page
        binding.signOutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        //exit button goes to homepage
        binding.exitButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, HomePage::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        })
    }
}
