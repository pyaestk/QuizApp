package com.example.quizapp.model

data class Quizzes (
    val question: String,
    val ansA: String,
    val ansB: String,
    val ansC: String,
    val ansD: String,
    val correctAnswer: String,
)