package com.example.traveleco.repo

import com.example.traveleco.ResponseMessage
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//class AuthRepositoryImpl @Inject constructor(
//    private val firebaseAuth: FirebaseAuth
//) : AuthRepository {
//    override fun loginUser(email: String, password: String): Flow<ResponseMessage<AuthResult>> {
//        return flow {
//            emit(ResponseMessage.Loading())
//            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
//            emit(ResponseMessage.Success(result))
//        }.catch {
//            emit(ResponseMessage.Error(it.message.toString()))
//        }
//
//    }
//
//    override fun registerUser(name: String, email: String, phoneNumber: String, password: String): Flow<ResponseMessage<AuthResult>> {
//        return flow {
//            emit(ResponseMessage.Loading())
//            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
//            emit(ResponseMessage.Success(result))
//        }.catch {
//            emit(ResponseMessage.Error(it.message.toString()))
//        }
//    }
//
//    override fun googleSignIn(credential: AuthCredential): Flow<ResponseMessage<AuthResult>> {
//        return flow {
//            emit(ResponseMessage.Loading())
//            val result = firebaseAuth.signInWithCredential(credential).await()
//            emit(ResponseMessage.Success(result))
//        }.catch {
//            emit(ResponseMessage.Error(it.message.toString()))
//        }
//    }
//
//
//}
