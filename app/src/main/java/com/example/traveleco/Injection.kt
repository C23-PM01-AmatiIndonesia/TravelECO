package com.example.traveleco

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.traveleco.repo.AuthRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

private const val NAME_DATASTORE = "traveleco"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(NAME_DATASTORE)
object Injection {
    fun provideRepository(context: Context): AuthRepository {
        val authPreference = AuthPreference.getInstance(context.dataStore)
        val firebaseAuth = FirebaseAuth.getInstance(FirebaseApp.getInstance())
        return AuthRepository.getInstance(authPreference, firebaseAuth)
    }
}