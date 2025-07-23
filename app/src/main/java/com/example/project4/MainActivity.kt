package com.example.project4

import android.content.Intent
import androidx.activity.OnBackPressedCallback
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.semantics.text
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

import com.example.project4.ItemsListFragment
import com.example.project4.LoginActivity
import com.example.project4.R

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close // Add these to strings.xml
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Set user name in Nav Header
        val headerView = navigationView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.textViewUserNameHeader)
        val userEmailTextView = headerView.findViewById<TextView>(R.id.textViewUserEmailHeader)

        // Get user name passed from Login/Splash or from Firebase directly
        val userNameFromIntent = intent.getStringExtra("USER_NAME")
        if (userNameFromIntent != null) {
            userNameTextView.text = userNameFromIntent
        } else {
            userNameTextView.text = currentUser?.displayName ?: "User"
        }
        userEmailTextView.text = currentUser?.email ?: ""


        // Load default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ItemsListFragment()) // Create ItemsListFragment later
                .commit()
            navigationView.setCheckedItem(R.id.nav_items_list)
        }
        val onBackPressedCallback = object : OnBackPressedCallback(true) { // true = enabled by default
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    if (isEnabled) { // Check if it's still enabled
                        isEnabled = false // Disable this callback
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }

        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var selectedFragment: Fragment? = null
        when (item.itemId) {
            R.id.nav_items_list -> {
                selectedFragment = ItemsListFragment()
            }
            R.id.nav_account -> {
                selectedFragment = AccountFragment()
                Toast.makeText(this, "Account selected", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
                mAuth.signOut()
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                return true
            }
        }

        if (selectedFragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit()
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}