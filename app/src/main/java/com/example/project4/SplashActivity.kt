package com.example.project4

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp


@SuppressLint("CustomSplashScreen") // Required if not using the new Splash Screen API for API 31+
class SplashActivity : ComponentActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.splashview) // Create a simple layout for your splash screen

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        Handler(Looper.getMainLooper()).postDelayed({
            if (user != null) {
                // User is signed in, navigate to MainActivity
                val mainIntent = Intent(this, MainActivity::class.java)
                // You can pass user info here if needed, though it's often better
                // to retrieve it again in MainActivity to ensure it's fresh.
                // mainIntent.putExtra("USER_NAME", user.displayName ?: user.email)
                startActivity(mainIntent)
            } else {
                // No user is signed in, navigate to LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish() // Finish SplashActivity so user can't navigate back to it
        }, 2000) // 2 seconds delay
    }
}
