package com.example.traveleco.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.traveleco.MainActivity
import com.example.traveleco.R
import com.example.traveleco.databinding.ActivityLoginBinding
import com.example.traveleco.model.AuthViewModel
import com.example.traveleco.ui.customview.EditButton
import com.example.traveleco.ui.customview.EditText
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: EditButton

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        authViewModel.loginResult.observe(this) { success ->
            if (success) {
                // kalo login sukses kirim lanjut ke main activity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }

        authViewModel.userData.observe(this) { user ->
            // Mengambil data pengguna dan kirim ke MainActivity
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra(EXTRA_EMAIL, user?.email)
            intent.putExtra(EXTRA_NAME, user?.name)
            startActivity(intent)
        }

        authViewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }


        loginEmail = binding!!.edLoginEmail
        loginPassword = binding!!.edLoginPassword

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        auth = FirebaseAuth.getInstance()

        binding?.btnIsRegister?.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        playAnimation()
        setupAction()
    }

    private fun setupAction() {
        binding?.apply {
            loginButton = btnLogin
            loginButton.setOnClickListener {
                if (loginEmail.error == null
                    && loginPassword.error == null
                    && loginEmail.text!!.isNotEmpty()
                    && loginPassword.text!!.isNotEmpty()) {
                    val email = binding?.edLoginEmail?.text.toString().trim()
                    val password = binding?.edLoginPassword?.text.toString().trim()
                    authViewModel.signInWithEmailAndPassword(email, password)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        R.string.check_validity_field,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun playAnimation() {
        val image = ObjectAnimator.ofFloat(binding?.imageView, View.ALPHA, 1F).setDuration(800)
        val email = ObjectAnimator.ofFloat(binding?.emailLayout, View.ALPHA, 1F).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding?.passwordLayout, View.ALPHA, 1F).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1F).setDuration(500)
        val bottomText = ObjectAnimator.ofFloat(binding?.tableLayout, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(image, email, password, btnLogin, bottomText)
            startDelay = 500
        }.start()
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_NAME = "extra_name"
    }

}
