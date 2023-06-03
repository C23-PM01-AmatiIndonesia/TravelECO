package com.example.traveleco.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.traveleco.MainActivity
import com.example.traveleco.R
import com.example.traveleco.databinding.ActivityBucketBinding
import com.example.traveleco.ui.auth.activity.LoginActivity
import com.example.traveleco.ui.auth.activity.PhoneActivity
import com.example.traveleco.ui.order.OrderActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class BucketActivity : AppCompatActivity(){
    private var _binding: ActivityBucketBinding? = null
    private val binding get() = _binding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBucketBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        val bottomNavigation = binding?.bottomNavigation
        @Suppress("DEPRECATION")
        bottomNavigation?.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigation?.selectedItemId = R.id.menu_add_to_cart

        supportActionBar?.hide()

//        val email = intent.getStringExtra(LoginActivity.EXTRA_EMAIL)
//        val displayName = intent.getStringExtra(LoginActivity.EXTRA_NAME)

//        binding?.tvWelcome?.text = "Hello $displayName, email $email"

        auth = FirebaseAuth.getInstance()
    }

    @Suppress("DEPRECATION")
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_add_to_cart -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_menu -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}