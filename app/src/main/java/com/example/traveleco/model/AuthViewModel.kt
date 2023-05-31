package com.example.traveleco.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveleco.*
import com.example.traveleco.repo.AuthRepository
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.launch

//class AuthViewModel : ViewModel() {
//
//    private val auth = FirebaseAuth.getInstance()
//    private val database = FirebaseDatabase.getInstance().reference.child(USERS_CHILD)
//
//    private val _loginResult = MutableLiveData<Boolean>()
//    val loginResult: LiveData<Boolean> get() = _loginResult
//
//    private val _registerResult = MutableLiveData<Boolean>()
//    val registerResult: LiveData<Boolean> get() = _registerResult
//
//    private val _userData = MutableLiveData<Users?>()
//    val userData: LiveData<Users?> get() = _userData
//
//    private val _errorMessage = MutableLiveData<String>()
//    val errorMessage: LiveData<String> get() = _errorMessage
//
//    fun signInWithEmailAndPassword(email: String, password: String) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                _loginResult.value = task.isSuccessful
//                if (task.isSuccessful) {
//                    val currentUserUid = auth.currentUser?.uid
//                    if (currentUserUid != null) {
//                        fetchUserData(currentUserUid)
//                        Log.d(TAG, "onFailure: ${loginResult.value.toString()}")
//                    } else {
//                        Log.d(TAG, "onFailure: currentUserUID is null")
//                    }
//                } else {
//                    when (task.exception?.message) {
//                        "There is no user record corresponding to this identifier. The user may have been deleted." -> {
//                            _errorMessage.value = "Akun Tidak ditemukan"
//                        }
//                        "The password is invalid or the user does not have a password." -> {
//                            _errorMessage.value = "Email atau password salah"
//                        }
//                        else -> {
//                            _errorMessage.value = "Gagal melakukan login"
//                        }
//                    }
//                    Log.d(TAG, "onFailure: ${task.isCanceled}")
//                }
//            }
//    }
//
//    private fun fetchUserData(uid: String) {
//        database.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    val user = dataSnapshot.getValue(Users::class.java)
//                    _userData.value = user
//                } else {
//                    _errorMessage.value = "Gagal Masuk"
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                _errorMessage.value = "Gagal Masuk"
//                Log.d(TAG, "onFailure: ${databaseError.message}")
//            }
//        })
//    }
//
//    fun registerUser(
//        name: String,
//        email: String,
//        phoneNumber: String,
//        country: String,
//        password: String) {
//        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val currentUserUid = auth.currentUser?.uid
//                if (currentUserUid != null) {
//                    val user = Users(name, email, phoneNumber, country, currentUserUid)
//                    database.child(currentUserUid).setValue(user).addOnCompleteListener {
//                        if (it.isSuccessful) {
//                            _registerResult.value = true
//                            _errorMessage.value = "Berhasil membuat akun"
//                        } else {
//                            _registerResult.value = false
//                            _errorMessage.value = "Gagal membuat akun"
//                        }
//                    }
//                }
//            } else {
//                when (task.exception?.message) {
//                    "The email address is already in use by another account." -> {
//                        _registerResult.value = false
//                        _errorMessage.value = "Email sudah digunakan"
//                    }
//                    else -> {
//                        _registerResult.value = false
//                        _errorMessage.value = "Gagal membuat akun"
//                    }
//                }
//            }
//        }
//    }
//
//
//    companion object {
//        private const val TAG = "AuthViewModel"
//        private const val USERS_CHILD = "users"
//    }
//
//}

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun getUser(): LiveData<AuthUser> = authRepository.getUser()

    fun saveUser(user: AuthUser) {
        viewModelScope.launch {
            authRepository.saveUser(user)
        }
    }

    fun userLogin(email: String, password: String) = authRepository.loginUser(email, password)

    fun userSignup(email: String, password: String) =
        authRepository.registerUser(email, password)

    fun googleSignIn(credential: AuthCredential) =
        authRepository.googleSignIn(credential)

    fun login() {
        viewModelScope.launch {
            authRepository.login()
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

}