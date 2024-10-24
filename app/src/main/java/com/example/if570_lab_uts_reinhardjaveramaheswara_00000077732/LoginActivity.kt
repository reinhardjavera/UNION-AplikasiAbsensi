package com.example.if570_lab_uts_reinhardjaveramaheswara_00000077732

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.view.View

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailEditText).text.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()
            validateAndLogin(email, password)
        }

        val signUpRedirectText = findViewById<TextView>(R.id.signUpRedirectText)
        signUpRedirectText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateAndLogin(email: String, password: String) {
        val emailErrorText = findViewById<TextView>(R.id.emailErrorText)
        val passwordErrorText = findViewById<TextView>(R.id.passwordErrorText)

        // Reset error messages
        emailErrorText.visibility = View.GONE
        passwordErrorText.visibility = View.GONE

        var isValid = true

        // Validate email field
        if (email.isEmpty()) {
            emailErrorText.text = "Email tidak boleh kosong"
            emailErrorText.visibility = View.VISIBLE
            isValid = false
        }

        // Validate password field
        if (password.isEmpty()) {
            passwordErrorText.text = "Password tidak boleh kosong"
            passwordErrorText.visibility = View.VISIBLE
            isValid = false
        } else if (password.length < 8) {
            passwordErrorText.text = "Password harus terdiri dari minimal 8 karakter"
            passwordErrorText.visibility = View.VISIBLE
            isValid = false
        }

        // If valid, proceed to login
        if (isValid) {
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                    // Menggunakan Intent ke MainActivity, dan dari sana bisa diarahkan ke HomeFragment
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

}