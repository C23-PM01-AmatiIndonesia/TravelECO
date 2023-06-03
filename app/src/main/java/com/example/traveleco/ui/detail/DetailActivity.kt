package com.example.traveleco.ui.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.traveleco.MainActivity
import com.example.traveleco.adapter.DestinationAdapter
import com.example.traveleco.adapter.SingleTripAdapter
import com.example.traveleco.database.*
import com.example.traveleco.databinding.ActivityDetailBinding
import com.example.traveleco.ui.auth.activity.PhoneActivity
import com.example.traveleco.ui.maps.MapsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var singleTrips: ArrayList<SinglePrograms>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        getDestinationData()
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding?.rvPackageSingle?.layoutManager = layoutManager

        binding?.btnLoc?.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        singleTrips = arrayListOf()
        getListPackage()
    }

    private fun getDestinationData() {
        database = FirebaseDatabase.getInstance().getReference(DATABASE_DESTINATION)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (destinationSnapshot in snapshot.children) {
                        val destination = destinationSnapshot.getValue(Destination::class.java)
                        Glide.with(applicationContext)
                            .load(destination?.photo_url)
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

    private fun getListPackage() {
        database = FirebaseDatabase.getInstance().getReference("activity_package").child("single_trip")
        database.get().addOnSuccessListener {
            for (data in it.children) {
                val singleTrip = data.getValue(SinglePrograms::class.java)
                singleTrips.add(singleTrip!!)
            }
            binding?.rvPackageSingle?.adapter = SingleTripAdapter(singleTrips)
            for (single in singleTrips) {
                Log.d("detail activity", "photo : ${single.photo_url}")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "DetailActivity"
        private const val DATABASE_DESTINATION = "destination"
    }

}