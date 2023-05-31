package com.example.traveleco

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) {
            editor.putBoolean(KEY_IS_LOGGED_IN, value)
            editor.apply()
        }

    fun clearSession() {
        editor.clear()
        editor.apply()
    }

    companion object {
        private const val PREF_NAME = "LoginSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    }
}
