//package com.example.traveleco.model
//
//import android.widget.Toast
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.example.traveleco.database.Users
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.FirebaseDatabase
//
//class RegisterViewModel : ViewModel() {
//
//    private val auth = FirebaseAuth.getInstance()
//    private val database = FirebaseDatabase.getInstance().reference.child(USERS_CHILD)
//
//    private val _registerResult = MutableLiveData<Boolean>()
//    val registerResult: LiveData<Boolean> get() = _registerResult
//
//    private val _errorMessage = MutableLiveData<String>()
//    val errorMessage: LiveData<String> get() = _errorMessage
//
//    fun registerUser(name: String, email: String, phoneNumber: String, password: String) {
//        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val currentUserUid = auth.currentUser?.uid
//                if (currentUserUid != null) {
//                    val user = Users(name, email, phoneNumber, currentUserUid)
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
//    companion object {
//        private const val USERS_CHILD = "users"
//    }
//
//}
