package com.example.traveleco

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.traveleco.database.Destination
import com.example.traveleco.databinding.ActivityMainBinding
import com.example.traveleco.ui.BucketActivity
import com.example.traveleco.ui.ProfileActivity
import com.example.traveleco.ui.auth.activity.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var destinationArrayList: ArrayList<Destination>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        val bottomNavigation = binding?.bottomNavigation
        @Suppress("DEPRECATION")
        bottomNavigation?.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigation?.selectedItemId = R.id.menu_home

        val email = intent.getStringExtra(LoginActivity.EXTRA_EMAIL)
        val displayName = intent.getStringExtra(LoginActivity.EXTRA_NAME)

        auth = FirebaseAuth.getInstance()

        val layoutManager = LinearLayoutManager(this)
        binding?.rvDestination?.layoutManager = layoutManager
        binding?.rvDestination?.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
        destinationArrayList = arrayListOf()
        getDestinationData()


    }

    private fun getDestinationData() {
        database = FirebaseDatabase.getInstance().getReference(DATABASE_NAME)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (destinationSnapshot in snapshot.children) {
                        val destination = destinationSnapshot.getValue(Destination::class.java)
                        destinationArrayList.add(destination!!)
                    }
                    binding?.rvDestination?.adapter = DestinationAdapter(destinationArrayList)
                } else {
                    Log.d(TAG, "Tidak Ada Data")
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    @Suppress("DEPRECATION")
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_add_to_cart -> {
                    startActivity(Intent(this, BucketActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    companion object {
        private const val TAG = "DetailActivity"
        private const val DATABASE_NAME = "destination"
    }

}

