package com.example.quizapp

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.quizapp.Quizs
import com.example.quizapp.databinding.ActivityQuizBinding
import com.google.android.play.integrity.internal.t
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class QuizActivity : AppCompatActivity() {

    lateinit var quizBinding: ActivityQuizBinding

    val database = FirebaseDatabase.getInstance()
    val databaseReference = database.reference.child("questions")

    var correctAnswer = ""

    var questionCount = 0
    var questionNumber = 1

    var userAnswer = ""

    var correctCount = 0
    var wrongCount = 0

    private lateinit var timer: CountDownTimer
    private val startTimerInMillis: Long = 30000
    private var timeLeftInMillis: Long = startTimerInMillis

    var timeContinue = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        val view = quizBinding.root
        setContentView(view)

        retrieveQuizFromDatabase()

        quizBinding.apply {
            progressBar.visibility = View.VISIBLE
            linearLayoutInfo.visibility = View.INVISIBLE
            linearLayoutQuestion.visibility = View.INVISIBLE
            buttonNextQuestion.visibility = View.INVISIBLE
            buttonFinish.visibility = View.INVISIBLE


            buttonNextQuestion.setOnClickListener {
                resetTimer()
                retrieveQuizFromDatabase()
            }

            textViewA.setOnClickListener{
                userAnswer = "a"
                decideAnswer(userAnswer, textViewA)
                buttonNextQuestion.visibility = View.VISIBLE
                buttonFinish.visibility = View.VISIBLE
                pauseTimer()
            }
            textViewB.setOnClickListener{
                userAnswer = "b"
                decideAnswer(userAnswer, textViewB)
                buttonNextQuestion.visibility = View.VISIBLE
                buttonFinish.visibility = View.VISIBLE
                pauseTimer()
            }
            textViewC.setOnClickListener{
                userAnswer = "c"
                decideAnswer(userAnswer, textViewC)
                buttonNextQuestion.visibility = View.VISIBLE
                buttonFinish.visibility = View.VISIBLE
                pauseTimer()
            }
            textViewD.setOnClickListener{
                userAnswer = "d"
                decideAnswer(userAnswer, textViewD)
                buttonNextQuestion.visibility = View.VISIBLE
                buttonFinish.visibility = View.VISIBLE
                pauseTimer()
            }
        }
    }



    private fun retrieveQuizFromDatabase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                questionCount = snapshot.childrenCount.toInt()

                if (questionNumber <= questionCount) {
                    nextQuestionOperation()
                    val quizQuestionSnapshot = snapshot.child(questionNumber.toString())
                    val quizQuestion = Quizs(
                        quizQuestionSnapshot.child("q").value.toString(),
                        quizQuestionSnapshot.child("a").value.toString(),
                        quizQuestionSnapshot.child("b").value.toString(),
                        quizQuestionSnapshot.child("c").value.toString(),
                        quizQuestionSnapshot.child("d").value.toString(),
                        quizQuestionSnapshot.child("answer").value.toString()
                    )

                    // Update UI with quizQuestion
                    updateUIWithQuestion(quizQuestion)

                    //start timer
                    startTimer()
                } else {
                    Toast.makeText(applicationContext, "You've answered all questions", Toast.LENGTH_SHORT).show()
                }
                questionNumber++
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun updateUIWithQuestion(quizQuestion: Quizs) {
        quizBinding.apply {
            textViewQuestion.text = quizQuestion.question
            textViewA.text = quizQuestion.ansA
            textViewB.text = quizQuestion.ansB
            textViewC.text = quizQuestion.ansC
            textViewD.text = quizQuestion.ansD

            // Update other UI elements
            progressBar.visibility = View.INVISIBLE
            linearLayoutInfo.visibility = View.VISIBLE
            linearLayoutQuestion.visibility = View.VISIBLE
            buttonNextQuestion.visibility = View.INVISIBLE
            buttonFinish.visibility = View.INVISIBLE
        }
        correctAnswer = quizQuestion.correctAnswer
    }

    fun decideAnswer(answer: String, view: View) {
        
        if(correctAnswer == answer) {
            view.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            correctCount++
            quizBinding.textViewCorrect.text = correctCount.toString()
        } else {
            view.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
            wrongCount++
            quizBinding.textViewWrong.text = wrongCount.toString()
            findAnswer()
        }

        disabledClick()

    }
    fun findAnswer() {
        when(correctAnswer) {
            "a" -> quizBinding.textViewA.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            "b" -> quizBinding.textViewB.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            "c" -> quizBinding.textViewC.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
            "d" -> quizBinding.textViewD.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
        }
    }
    fun disabledClick(){
        quizBinding.apply {
            textViewA.isClickable = false
            textViewB.isClickable = false
            textViewC.isClickable = false
            textViewD.isClickable = false
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun nextQuestionOperation() {
        quizBinding.apply {
            textViewA.backgroundTintList = null
            textViewB.backgroundTintList = null
            textViewC.backgroundTintList = null
            textViewD.backgroundTintList = null

            textViewA.isClickable = true
            textViewB.isClickable = true
            textViewC.isClickable = true
            textViewD.isClickable = true
        }
    }

    //for timer
    private fun startTimer() {
        timer = object: CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(remainingTime: Long) {
                timeLeftInMillis = remainingTime
                updateText()
            }

            override fun onFinish() {

                resetTimer()
                updateText()
                disabledClick()
                
                quizBinding.apply {
                    textViewQuestion.text = "Sorry, Time is Up!"
                    buttonNextQuestion.visibility = View.VISIBLE
                    buttonFinish.visibility = View.VISIBLE
                }
                
            }

        }.start()

        timeContinue = true
    }

    private fun resetTimer() {
        pauseTimer()
        timeLeftInMillis = startTimerInMillis
    }
    private fun pauseTimer() {
        timer.cancel()
        timeContinue = false
    }

    private fun updateText() {
        val remainingTime: Int = (timeLeftInMillis / 1000).toInt()
        quizBinding.textViewTime.text = String.format(Locale.getDefault(), "%02d", remainingTime)
    }
}