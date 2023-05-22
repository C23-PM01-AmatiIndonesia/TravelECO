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
import com.example.traveleco.R
import com.example.traveleco.database.Users
import com.example.traveleco.databinding.ActivityRegisterBinding
import com.example.traveleco.model.AuthViewModel
import com.example.traveleco.ui.customview.EditButton
import com.example.traveleco.ui.customview.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var users: Users? = null

    private lateinit var signupName: EditText
    private lateinit var signupEmail: EditText
    private lateinit var signupPhoneNumber: EditText
    private lateinit var signupPassword: EditText
    private lateinit var signupButton: EditButton

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

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
        database = FirebaseDatabase.getInstance()

        signupName = binding!!.edRegisterName
        signupEmail = binding!!.edRegisterEmail
        signupPhoneNumber = binding!!.edRegisterPhoneNumber
        signupPassword = binding!!.edRegisterPassword
        signupButton = binding!!.btnSignup
        val isLogin = binding!!.txtIsLogin

        isLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        signupButton.setOnClickListener {
            setupAction()
        }

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        authViewModel.registerResult.observe(this) { success ->
            if (success) {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    R.string.error_register,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        authViewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        playAnimation()
    }

    private fun setupAction() {
        binding?.apply {
            signupButton = btnSignup
            signupButton.setOnClickListener {
                if (edRegisterName.error == null
                    && edRegisterName.text!!.isNotEmpty()
                    && edRegisterPhoneNumber.error == null
                    && edRegisterPhoneNumber.text!!.isNotEmpty()
                    && edRegisterEmail.error == null
                    && edRegisterPassword.text!!.isNotEmpty()
                    && edRegisterPassword.error == null
                    && edRegisterPassword.text!!.isNotEmpty()
                ) {
                    val name = signupName.text.toString().trim()
                    val email = signupEmail.text.toString().trim()
                    val phoneNumber = signupPhoneNumber.text.toString()
                    val password = signupPassword.text.toString().trim()
                    authViewModel.registerUser(name, email, phoneNumber, password)
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        R.string.check_validity_field,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding?.titleTextView, View.ALPHA, 1F).setDuration(500)
        val message = ObjectAnimator.ofFloat(binding?.messageTextView, View.ALPHA, 1F).setDuration(500)
        val username = ObjectAnimator.ofFloat(binding?.nameLayout, View.ALPHA, 1F).setDuration(500)
        val phoneNumber = ObjectAnimator.ofFloat(binding?.phoneLayout, View.ALPHA, 1F).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding?.emailLayout, View.ALPHA, 1F).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding?.passwordLayout, View.ALPHA, 1F).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding?.btnSignup, View.ALPHA, 1F).setDuration(500)
        val bottomText = ObjectAnimator.ofFloat(binding?.tableLayout, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, message, username, phoneNumber, email, password, btnLogin, bottomText)
            startDelay = 500
        }.start()
    }
}