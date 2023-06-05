package com.example.traveleco.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.traveleco.MainActivity
import com.example.traveleco.R
import com.example.traveleco.adapter.BucketAdapter
import com.example.traveleco.database.ListBucket
import com.example.traveleco.databinding.ActivityBucketBinding
import com.example.traveleco.ui.auth.activity.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BucketActivity : AppCompatActivity(){
    private var _binding: ActivityBucketBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var favoriteList: ArrayList<ListBucket>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBucketBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val bottomNavigation = binding?.bottomNavigation
        @Suppress("DEPRECATION")
        bottomNavigation?.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigation?.selectedItemId = R.id.menu_add_to_cart

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        val layoutManager = LinearLayoutManager(this)
        binding?.rvBucket?.layoutManager = layoutManager
        binding?.rvBucket?.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
        favoriteList = arrayListOf()
        getFavoriteData()
    }

    private fun getFavoriteData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            database = FirebaseDatabase.getInstance().getReference("users").child(userId).child("bucket")
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    favoriteList.clear() // Clear the list before adding new data
                    for (favoriteSnapshot in snapshot.children) {
                        val favorite = favoriteSnapshot.getValue(ListBucket::class.java)
                        favoriteList.add(favorite!!)
                    }
                    binding?.rvBucket?.adapter?.notifyDataSetChanged()
                    binding?.rvBucket?.adapter = BucketAdapter(favoriteList)
                    if (favoriteList.isEmpty()) {
                        binding?.tvEmptyBucket?.visibility = View.VISIBLE
                    } else {
                        binding?.tvEmptyBucket?.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@BucketActivity, "Gagal", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    @Suppress("DEPRECATION")
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_add_to_cart -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_receipt -> {
                    startActivity(Intent(this, ReceiptActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_menu -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
