package com.example.traveleco.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.traveleco.database.Destination
import com.example.traveleco.databinding.ActivityDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = FirebaseAuth.getInstance()
        getDestinationData()
    }

    private fun getDestinationData() {
        database = FirebaseDatabase.getInstance().getReference(DATABASE_NAME)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (destinationSnapshot in snapshot.children) {
                        val destination = destinationSnapshot.getValue(Destination::class.java)
                        Glide.with(applicationContext)
                            .load(destination?.photoUrl)
                            .into(binding!!.ivDestinationDetail)
                        binding?.tvDetail?.text = destination?.name
                        binding?.tvLocation?.text = destination?.location
                        binding?.tvDescription?.text = destination?.longDescription
                    }
                } else {
                    Log.d(TAG, "gaada data")
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    companion object {
        private const val TAG = "DetailActivity"
        private const val DATABASE_NAME = "destination"
    }

}