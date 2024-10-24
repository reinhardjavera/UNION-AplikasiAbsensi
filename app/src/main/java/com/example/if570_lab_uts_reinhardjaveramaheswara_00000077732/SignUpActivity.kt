package com.example.if570_lab_uts_reinhardjaveramaheswara_00000077732

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.view.View

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailEditTextSignUp).text.toString()
            val password = findViewById<EditText>(R.id.passwordEditTextSignUp).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.confirmPasswordEditTextSignUp).text.toString()
            validateInput(email, password, confirmPassword)
        }

        val loginRedirectText = findViewById<TextView>(R.id.loginRedirectText)
        loginRedirectText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInput(email: String, password: String, confirmPassword: String) {
        val emailErrorText = findViewById<TextView>(R.id.emailErrorText)
        val passwordErrorText = findViewById<TextView>(R.id.passwordErrorText)
        val confirmPasswordErrorText = findViewById<TextView>(R.id.confirmPasswordErrorText)

        // Reset error messages
        emailErrorText.visibility = View.GONE
        passwordErrorText.visibility = View.GONE
        confirmPasswordErrorText.visibility = View.GONE

        var isValid = true

        if (email.isEmpty()) {
            emailErrorText.text = "Email tidak boleh kosong"
            emailErrorText.visibility = View.VISIBLE
            isValid = false
        }

        if (password.isEmpty()) {
            passwordErrorText.text = "Password tidak boleh kosong"
            passwordErrorText.visibility = View.VISIBLE
            isValid = false
        } else if (password.length < 8) {
            passwordErrorText.text = "Password harus terdiri dari minimal 8 karakter"
            passwordErrorText.visibility = View.VISIBLE
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordErrorText.text = "Confirm Password tidak boleh kosong"
            confirmPasswordErrorText.visibility = View.VISIBLE
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordErrorText.text = "Passwords tidak cocok"
            confirmPasswordErrorText.visibility = View.VISIBLE
            isValid = false
        }

        if (isValid) {
            signUpUser(email, password)
        }
    }

    private fun signUpUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid

                    // Save additional user info to Firestore
                    val userData = hashMapOf(
                        "email" to email
                    )

                    uid?.let {
                        db.collection("users").document(it)
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Sign Up Berhasil!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Gagal untuk menyimpan data pengguna: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Sign Up Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}