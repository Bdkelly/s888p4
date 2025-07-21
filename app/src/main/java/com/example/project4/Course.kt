package com.example.project4.models // Create a models package

// Add @IgnoreExtraProperties if using Firebase Realtime Database and you have extra fields
// import com.google.firebase.database.IgnoreExtraProperties

// @IgnoreExtraProperties
data class Course(
    val id: String? = null, // For Firebase key
    val name: String? = null,
    val description: String? = null,
    val instructor: String? = null
    // Add other properties as needed
) {
    // Add a no-argument constructor for Firebase deserialization
    constructor() : this("", "", "", "")
}