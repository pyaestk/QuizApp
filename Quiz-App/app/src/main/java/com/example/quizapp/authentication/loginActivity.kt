package com.example.quizapp.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.quizapp.MainActivity
import com.example.quizapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class loginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding

    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)

        supportActionBar?.title = "Login your account"
        registerActivityForGSB()
        loginBinding.apply {

            buttonSignIn.setOnClickListener {
                val userEmail = editTextLoginEmail.text.toString()
                val userPassword = editTextLoginPassword.text.toString()

                if (userEmail.isEmpty() || userPassword.isEmpty()) {
                    Toast.makeText(applicationContext, "Enter valid email and password", Toast.LENGTH_SHORT).show()
                } else {
                    SignInFirebaseUser(userEmail, userPassword)
                }
            }

            
            signInGoogle.setOnClickListener {
                signInWithGoogle()
            }

            textViewSignUp.setOnClickListener {
                val intent = Intent(this@loginActivity, SignUpActivity::class.java)
                startActivity(intent)
            }

            textViewForgotPass.setOnClickListener {
                val intent = Intent(this@loginActivity, ForgotPasswordActivity::class.java)
                startActivity(intent)
            }

        }
    }



    fun SignInFirebaseUser(userEmail: String, userPassword: String) {

        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@loginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(applicationContext, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    //for google signIn
    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("867623466561-oj8ielmqvik3stl0i61k5160ejaelf1e.apps.googleusercontent.com")
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        signIn()
    }

    private fun signIn() {
        val signInIntent : Intent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)
    }
    private fun registerActivityForGSB() {

        activityResultLauncher = registerForActivityResult( ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->

                val resultCode = result.resultCode
                val data = result.data

                if(resultCode == RESULT_OK && data != null) {

                    val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                    firebaseSignInWithGoogle(task)
                }
            }
        )
    }

    private fun firebaseSignInWithGoogle(task: Task<GoogleSignInAccount>) {

        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            firebaseGoogleAccount(account)
            finish()
        } catch (e: ApiException) {
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseGoogleAccount(account: GoogleSignInAccount) {

        val authCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(authCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //val user = auth.currentUser
            } else {

            }
        }

    }


    override fun onStart() {
        super.onStart()

        val user = auth.currentUser

        if(user != null) {
            Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@loginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}