package com.example.traveleco

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.traveleco.adapter.DestinationAdapter
import com.example.traveleco.database.Destination
import com.example.traveleco.database.Users
import com.example.traveleco.databinding.ActivityMainBinding
import com.example.traveleco.ui.BucketActivity
import com.example.traveleco.ui.ProfileActivity
import com.example.traveleco.ui.auth.activity.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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

        supportActionBar?.hide()

        val bottomNavigation = binding?.bottomNavigation
        @Suppress("DEPRECATION")
        bottomNavigation?.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigation?.selectedItemId = R.id.menu_home

        auth = FirebaseAuth.getInstance()

        val layoutManager = LinearLayoutManager(this)
        binding?.rvDestination?.layoutManager = layoutManager
        binding?.rvDestination?.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
        destinationArrayList = arrayListOf()
        getDestinationData()

        database = FirebaseDatabase.getInstance().reference.child("users")

        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        database.child(currentUserUid!!).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(Users::class.java)
                    binding?.tvUsername?.text = user?.name
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ProfileActivity", "Gagal")
            }
        })

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

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Gagal", Toast.LENGTH_SHORT).show()
            }
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
                    val email = intent.getStringExtra(LoginActivity.EXTRA_EMAIL)
                    val displayName = intent.getStringExtra(LoginActivity.EXTRA_NAME)
                    val emailGoogle = intent.getStringExtra(LoginActivity.EMAIL_GOOGLE)
                    val displayNameGoogle = intent.getStringExtra(LoginActivity.NAME_GOOGLE)
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra(EXTRA_NAME, displayName)
                    intent.putExtra(EXTRA_EMAIL, email)
                    intent.putExtra(NAME_GOOGLE, displayNameGoogle)
                    intent.putExtra(EMAIL_GOOGLE, emailGoogle)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

//    override fun onBackPressed() {
//        moveTaskToBack(true)
//    }

    companion object {
        private const val TAG = "DetailActivity"
        private const val DATABASE_NAME = "destination"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_NAME = "extra_name"
        const val NAME_GOOGLE = "name_google"
        const val EMAIL_GOOGLE = "email_google"
    }

}

