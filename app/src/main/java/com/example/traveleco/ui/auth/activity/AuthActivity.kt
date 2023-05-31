package com.example.traveleco.ui.auth.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.traveleco.MainActivity
import com.example.traveleco.model.ViewModelFactory
import com.example.traveleco.databinding.ActivityMainBinding
import com.example.traveleco.model.AuthViewModel

class AuthActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding
    private lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

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