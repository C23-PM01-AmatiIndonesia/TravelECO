package com.example.traveleco.ui.auth.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.traveleco.*
import com.example.traveleco.databinding.ActivityLoginBinding
import com.example.traveleco.model.AuthViewModel
import com.example.traveleco.model.ViewModelFactory
import com.example.traveleco.ui.auth.pref.AuthUser
import com.example.traveleco.ui.customview.EditButton
import com.example.traveleco.ui.customview.EditText
import com.example.traveleco.util.ResponseMessage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: EditButton
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

        setupModel()

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
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding?.btnIsRegister?.setOnClickListener {
            val intent = Intent(this, PhoneActivity::class.java)
            startActivity(intent)
        }

        val googleTextView: TextView = binding?.loginWithGoogle?.getChildAt(0) as TextView
        googleTextView.text = resources.getString(R.string.button_google_text)

        binding?.loginWithGoogle?.setOnClickListener {
            signInWithGoogle()
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
//                    authViewModel.signInWithEmailAndPassword(email, password)
                    authViewModel.userLogin(email, password).observe(this@LoginActivity) { response ->
                        when (response) {
                            is ResponseMessage.Loading -> {}
                            is ResponseMessage.Success -> {
                                saveUserData(
                                    AuthUser(
                                        response.data?.user?.displayName.toString(),
                                        response.data?.user?.uid.toString(),
                                        true
                                    )
                                )
                                if (auth.currentUser!!.isEmailVerified) {
                                    val intent =
                                        Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Kamu belum verifikasi Email. Silahkan cek pada Email Kamu untuk dapat melanjutkannya",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is ResponseMessage.Error -> {
                                Log.d(
                                    "OnErrorLogin: ",
                                    "response: ${response.message.toString()}"
                                )
                                when (response.message) {
                                    "There is no user record corresponding to this identifier. The user may have been deleted" ->
                                        Toast.makeText(this@LoginActivity, "Akun Tidak ditemukan", Toast.LENGTH_SHORT).show()
                                    "The password is invalid or the user does not have a password" ->
                                        Toast.makeText(this@LoginActivity, "Email atau password salah", Toast.LENGTH_SHORT).show()
                                    "The email address is badly formatted" ->
                                        Toast.makeText(this@LoginActivity, "Email atau password salah", Toast.LENGTH_SHORT).show()
                                    else ->
                                        Toast.makeText(this@LoginActivity, "Akun tidak terdaftar", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
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

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                authViewModel.googleSignIn(credential).observe(this@LoginActivity) { response ->
                    when (response) {
                        is ResponseMessage.Loading -> {}
                        is ResponseMessage.Success -> {
                            saveUserData(
                                AuthUser(
                                    response.data?.user?.displayName.toString(),
                                    response.data?.user?.uid.toString(),
                                    true
                                )
                            )
                            Log.d("LoginActivity", "Memulai MainActivity")
                            val intent = Intent(this@LoginActivity, CountryActivity::class.java)
                            intent.putExtra(NAME_GOOGLE, account.displayName)
                            intent.putExtra(EMAIL_GOOGLE, account.email)
                            startActivity(intent)
                            finish()
                        }
                        is ResponseMessage.Error -> {
                            Log.d("OnErrorLogin: ", "response: ${response.message.toString()}")
                            Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserData(user: AuthUser) {
        authViewModel.saveUser(user)
    }

    private fun setupModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun playAnimation() {
        val text = ObjectAnimator.ofFloat(binding?.welcomeText, View.ALPHA, 1F).setDuration(800)
        val email = ObjectAnimator.ofFloat(binding?.emailLayout, View.ALPHA, 1F).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding?.passwordLayout, View.ALPHA, 1F).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1F).setDuration(500)
        val orText = ObjectAnimator.ofFloat(binding?.orText, View.ALPHA, 1F).setDuration(500)
        val btnWithGoogle = ObjectAnimator.ofFloat(binding?.loginWithGoogle, View.ALPHA, 1F).setDuration(500)
        val bottomText = ObjectAnimator.ofFloat(binding?.tableLayout, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(text, email, password, btnLogin, orText, btnWithGoogle, bottomText)
            startDelay = 500
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            edLoginEmail.isEnabled = !isLoading
            edLoginPassword.isEnabled = !isLoading
            btnLogin.isEnabled = !isLoading
            progressBar.visibility = View.VISIBLE
        }
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_NAME = "extra_name"
        const val NAME_GOOGLE = "name_google"
        const val EMAIL_GOOGLE = "email_google"
    }

}
