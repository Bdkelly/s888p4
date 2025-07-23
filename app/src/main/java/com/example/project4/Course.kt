package com.example.project4.models // Create a models package

data class Course(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val instructor: String? = null

) {
    constructor() : this("", "", "", "")
}