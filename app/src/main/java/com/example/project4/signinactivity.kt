package com.example.project4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpActivity : ComponentActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signinview)

        mAuth = FirebaseAuth.getInstance()

        nameEditText = findViewById(R.id.editTextNameSignUp)
        emailEditText = findViewById(R.id.editTextEmailSignUp)
        passwordEditText = findViewById(R.id.editTextPasswordSignUp)
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPasswordSignUp)
        signUpButton = findViewById(R.id.buttonSignUpConfirm)

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = mAuth.currentUser
                        // Update user profile with name
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        firebaseUser?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {

                                    Toast.makeText(this, "Account created.", Toast.LENGTH_SHORT).show()

                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finishAffinity()
                                }
                            }
                    } else {
                        Toast.makeText(baseContext, "Sign up failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}