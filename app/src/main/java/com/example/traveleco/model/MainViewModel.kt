package com.example.traveleco.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class MainViewModel : ViewModel() {
    private val _userToken = MutableLiveData<String>()
    val userToken: LiveData<String> get() = _userToken

    private val auth = FirebaseAuth.getInstance()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.getIdToken(true).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result?.token
                        _userToken.value = token
                    } else {
                        _userToken.value = null
                    }
                }
            } else {
                _userToken.value = null
            }
        }
    }

    fun clearUserToken() {
        _userToken.value = null
    }
}
