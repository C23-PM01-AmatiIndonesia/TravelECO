//package com.example.traveleco.ui.favorite
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.DividerItemDecoration
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.traveleco.database.ListBucket
//import com.example.traveleco.databinding.ActivityMainBinding
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//
//class FavoriteActivity : AppCompatActivity() {
//
//    private var _binding: ActivityMainBinding? = null
//    private val binding get() = _binding
//
//    private lateinit var database: DatabaseReference
//    private lateinit var favoriteList: ArrayList<ListBucket>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        _binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding?.root)
//
//        // Inisialisasi Firebase Database
//        database = FirebaseDatabase.getInstance().reference
//
//
//        val layoutManager = LinearLayoutManager(this)
//        binding?.rvDestination?.layoutManager = layoutManager
//        binding?.rvDestination?.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
//        favoriteList = arrayListOf()
//        getFavoriteData()
//
//    }
//
//    private fun getFavoriteData() {
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//        if (userId != null) {
//            database = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites")
//
//        }
//    }
//}