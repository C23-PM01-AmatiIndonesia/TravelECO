package com.example.traveleco.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.traveleco.MainActivity
import com.example.traveleco.databinding.ActivitySplashScreenBinding
import com.example.traveleco.model.AuthViewModel
import com.example.traveleco.model.ViewModelFactory
import com.example.traveleco.ui.auth.activity.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupModel()
        authViewModel.getUser().observe(this) { user ->
            if (user.isLogin){
                Log.d("Auth Check: ", "Have OnLogin")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else {
                Log.d("Auth Check: ", "Haven't OnLogin")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
    private fun setupModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }
}