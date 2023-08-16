package com.example.quizapp.logic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quizapp.databinding.ActivityResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResultActivity : AppCompatActivity() {

    lateinit var resultBinding : ActivityResultBinding

    val database = FirebaseDatabase.getInstance()
    val databaseRef = database.reference.child("scores")

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var correctAnswer = ""
    var wrongAnswer = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultBinding = ActivityResultBinding.inflate(layoutInflater)
        val view = resultBinding.root
        setContentView(view)

        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user?.let {
                    val userUid = it.uid
                    correctAnswer = snapshot.child(userUid).child("correct").value.toString()
                    wrongAnswer = snapshot.child(userUid).child("wrong").value.toString()

                    resultBinding.apply {
                        resultCorrect.text = correctAnswer
                        resultWrong.text = wrongAnswer
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        resultBinding.buttonPlayAgain.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
            finish()
        }

        resultBinding.buttonHome.setOnClickListener {
            finish()
        }
    }
}