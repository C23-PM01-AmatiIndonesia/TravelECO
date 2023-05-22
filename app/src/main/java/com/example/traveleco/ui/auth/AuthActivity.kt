package com.example.traveleco.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.traveleco.MainActivity
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

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        authViewModel.userToken.observe(this) { token ->
            if (token != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // Pengguna harus login kembali
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

}