package com.example.project4 // Make sure this matches your project's package name

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.semantics.text
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AccountFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    private lateinit var textViewAccountName: TextView
    private lateinit var textViewAccountEmail: TextView
    private lateinit var buttonEditProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth.currentUser
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        textViewAccountName = view.findViewById(R.id.textViewAccountName)
        textViewAccountEmail = view.findViewById(R.id.textViewAccountEmail)
        buttonEditProfile = view.findViewById(R.id.buttonEditProfile)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate the views with user data
        currentUser?.let { user ->
            textViewAccountName.text = user.displayName ?: "N/A"
            textViewAccountEmail.text = user.email ?: "N/A"
        } ?: run {
            // Handle case where user is null (should ideally not happen if this fragment is protected)
            textViewAccountName.text = "Not logged in"
            textViewAccountEmail.text = ""
        }

        buttonEditProfile.setOnClickListener {
            // TODO: Implement profile editing functionality
            // For example, navigate to an EditProfileActivity or show a dialog
            Toast.makeText(context, "Edit Profile Clicked (Not Implemented)", Toast.LENGTH_SHORT).show()
        }
    }
}