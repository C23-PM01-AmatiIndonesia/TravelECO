//package com.example.traveleco.repo
//
//import androidx.lifecycle.LiveData
//import androidx.paging.ExperimentalPagingApi
//import androidx.paging.Pager
//import androidx.paging.PagingConfig
//import androidx.paging.PagingData
//import com.example.traveleco.DestinationDataSource
//import com.example.traveleco.database.Destination
//import com.google.firebase.database.FirebaseDatabase
//
//class DestinationRepository(
//    private val database: FirebaseDatabase
//) {
//
//
//
//    companion object {
//        @Volatile
//        private var instance: DestinationRepository? = null
//        fun getInstance(
//            database: FirebaseDatabase
//        ): DestinationRepository = instance ?: synchronized(this) {
//            instance ?: DestinationRepository(database)
//        }.also {
//            instance = it
//        }
//    }
//
//}