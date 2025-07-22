package com.example.project4

import android.app.Application
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
// Import the Debug Provider
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        val firebaseAppCheck = FirebaseAppCheck.getInstance()

        // Install the Debug Provider
        // Make sure you have the dependency in your build.gradle.kts:
        // implementation("com.google.firebase:firebase-appcheck-debug:LATEST_VERSION")
        // You will also need to get a debug token from Logcat and add it to the Firebase console.
        if (BuildConfig.DEBUG) { // Good practice to only use Debug provider in debug builds
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            // In a real app, you'd install a production provider like Play Integrity here for release builds.
            // If you truly want NO provider for release builds after removing Play Integrity,
            // then App Check will effectively be disabled for release.
        }
    }
}