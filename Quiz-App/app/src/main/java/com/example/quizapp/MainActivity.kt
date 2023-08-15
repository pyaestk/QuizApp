package com.example.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.quizapp.authentication.loginActivity
import com.example.quizapp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

//        val currentUser = FirebaseAuth.getInstance().currentUser
//        val userEmail = currentUser?.email
//
//        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
//        val userName = googleSignInAccount?.email // Retrieve the name from the GoogleSignInAccount
//
//        if (userEmail != null) {
//            mainBinding.textViewEmail.text = "$userEmail"
//        }
//        else if (userName != null) {
//            mainBinding.textViewEmail.text = "$userName"
//        }
//        else {
//            mainBinding.textViewEmail.text = "error"
//        }

        mainBinding.startQuiz.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }

        mainBinding.signOutButton.setOnClickListener {
            //for email and password log out
            FirebaseAuth.getInstance().signOut()


            //for google sign out
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener{task ->
                if(task.isSuccessful) {
                    Toast.makeText(applicationContext, "You've just signed out", Toast.LENGTH_SHORT).show()
                }
            }
            
            val intent = Intent(this@MainActivity, loginActivity::class.java)
            startActivity(intent)
            finish()
        }



    }
}
