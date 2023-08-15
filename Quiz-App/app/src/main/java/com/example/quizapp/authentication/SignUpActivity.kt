package com.example.quizapp.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.quizapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth


class SignUpActivity : AppCompatActivity() {


    private lateinit var signUpBinding: ActivitySignUpBinding

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = signUpBinding.root
        setContentView(view)

        supportActionBar?.title = "Register your account"

        signUpBinding.apply {

            buttonSignUp.setOnClickListener {
                val email = editTextSignUpEmail.text.toString()
                val password = editTextSignUpPassword.text.toString()

                if(email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(applicationContext, "Enter valid email and password", Toast.LENGTH_SHORT).show()
                } else {
                    signUpFirebase(email, password)
                }


            }

        }
    }

    private fun signUpFirebase(email: String, password: String) {

        signUpBinding.progressBar.visibility = View.VISIBLE
        signUpBinding.buttonSignUp.isClickable = false

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task->
            if(task.isSuccessful) {

                Toast.makeText(applicationContext, "Your account has been created", Toast.LENGTH_SHORT).show()
                finish()
                signUpBinding.progressBar.visibility = View.INVISIBLE
                signUpBinding.buttonSignUp.isClickable = true

            } else {

                Toast.makeText(this@SignUpActivity, task.exception?.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
                signUpBinding.progressBar.visibility = View.INVISIBLE
                signUpBinding.buttonSignUp.isClickable = true

            }
        }

    }
}