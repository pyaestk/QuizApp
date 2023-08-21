package com.example.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.quizapp.auth.loginActivity
import com.example.quizapp.databinding.ActivityMainBinding
import com.example.quizapp.logic.QuizActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding

    var correctCount = 0

    val database = FirebaseDatabase.getInstance()
    val databaseRef = database.reference.child("scores")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        //displaying current user
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        val userName = googleSignInAccount?.email // Retrieve the name from the GoogleSignInAccount

        if (userEmail != null) {
            mainBinding.textViewEmail.text = "$userEmail"
        }
        else if (userName != null) {
            mainBinding.textViewEmail.text = "$userName"
        }
        else {
            mainBinding.textViewEmail.text = "error"
        }

        //displaying current user's last result
        val userUid = currentUser?.uid ?: ""
        val userScoreRef = databaseRef.child(userUid)

        val scoreListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val correctCount = snapshot.child("correct").getValue(Long::class.java) ?: 0
                    val result = getString(R.string.last_result_template, correctCount)
                    mainBinding.textViewLastresult.text = result
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error here
            }
        }

        userScoreRef.addValueEventListener(scoreListener)

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
