//package com.example.traveleco
//
//import com.example.traveleco.database.Destination
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import kotlinx.coroutines.tasks.await
//
//class DestinationDataSource {
//    private val database: DatabaseReference = FirebaseDatabase
//        .getInstance()
//        .reference
//        .child(DESTINATION_DATABASE)
//
//    suspend fun getAllDestinationKeys(): List<String> {
//        val dataSnapshot = database.get().await()
//        val destinationKeys = mutableListOf<String>()
//
//        for (snapshot in dataSnapshot.children) {
//            val destinationKey = snapshot.key
//            if (destinationKey != null) {
//                destinationKeys.add(destinationKey)
//            }
//        }
//
//        return destinationKeys
//    }
//
//    fun getDestinationByKey(destinationKey: String): DatabaseReference {
//        return database.child(destinationKey)
//    }
//
//    companion object {
//        private const val DESTINATION_DATABASE = "tegaldukuh-packages"
//    }
//}