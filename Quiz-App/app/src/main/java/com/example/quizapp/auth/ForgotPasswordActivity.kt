package com.example.quizapp.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.quizapp.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var forgotPasswordBinding: ActivityForgotPasswordBinding

    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotPasswordBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = forgotPasswordBinding.root
        setContentView(view)

        supportActionBar?.title = "Forgot Password"

        forgotPasswordBinding.apply {

            buttonReset.setOnClickListener {
                val userEmail = textInputLayoutForgot.text.toString()

                if(userEmail.isEmpty()) {
                    Toast.makeText(applicationContext, "Enter valid email address", Toast.LENGTH_SHORT).show()
                } else {
                    auth.sendPasswordResetEmail(userEmail).addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            Toast.makeText(applicationContext, "We've sent a password reset mail to your email address", Toast.LENGTH_SHORT
                            ).show()

                            finish()

                        } else {
                            Toast.makeText(applicationContext, task.exception?.localizedMessage, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

    }
}