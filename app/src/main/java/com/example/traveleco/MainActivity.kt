package com.example.traveleco

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.traveleco.adapter.DestinationAdapter
import com.example.traveleco.data.CarbonResponse
import com.example.traveleco.database.Destination
import com.example.traveleco.databinding.ActivityMainBinding
import com.example.traveleco.model.CarbonViewModel
import com.example.traveleco.ui.BucketActivity
import com.example.traveleco.ui.ProfileActivity
import com.example.traveleco.ui.ReceiptActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.Manifest
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var destinationArrayList: ArrayList<Destination>
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var carbonViewModel: CarbonViewModel
    private var latitude: Double? = null
    private var longitude: Double? = null
    private val REQUEST_LOCATION_SETTINGS = 1
    private var carbonResponse = CarbonResponse()

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

        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val displayName = sharedPref.getString("displayName", "")

        database = FirebaseDatabase.getInstance().reference.child("users")
        carbonViewModel = ViewModelProvider(this)[CarbonViewModel::class.java]

        binding?.tvUsername?.text = displayName.toString()
        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        if (savedInstanceState == null) {
            checkLocationPermission()
        }

        getMyLocation()
    }

    private fun checkLocationPermission() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                getMyLocation()
            }
        } else {
            requestPermission()
        }
    }

    private fun getMyLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                locationProvider.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                        // Ambil longitude dari aplikasi Kotlin

                        val startAddress = "-6.29209, 106.8858777"
                        Log.d("MainActivity", startAddress)

                        Log.d("MainActivity", "${latitude.toString()}, ${longitude.toString()}")
                        carbonViewModel.getCarbonEmission("${latitude.toString()}, ${longitude.toString()}")
                        carbonViewModel.carbonEmissionLiveData.observe(this) { carbonEmission ->
                            carbonResponse = carbonEmission
                            if (carbonEmission != null) {
                                binding?.tvCarbon?.text = carbonEmission.predictedEmission.toString()
                            }
                        }

                    } else {
                        Toast.makeText(this, resources.getString(R.string.invalid_location), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, resources.getString(R.string.turn_on_loc), Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            // request permission
            requestPermission()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getMyLocation()
            } else {
                showLocationPermissionExplanation()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_LOCATION_SETTINGS) {
            if (isLocationEnabled()) {
                getMyLocation()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun showLocationPermissionExplanation() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.permission_loc))
        builder.setMessage(resources.getString(R.string.msg_permission))
        builder.setPositiveButton(resources.getString(R.string.allow_permission)) { dialog, _  ->
            dialog.dismiss()
            Toast.makeText(this, resources.getString(R.string.turn_on_loc), Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            @Suppress("DEPRECATION")
            startActivityForResult(intent, REQUEST_LOCATION_SETTINGS)
        }
        builder.setNegativeButton(resources.getString(R.string.failed)) { dialog, _  ->
            dialog.dismiss()
            finish()
        }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
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
                    Log.d(TAG, "No Data")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, resources.getString(R.string.failed), Toast.LENGTH_SHORT).show()
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
                R.id.menu_receipt -> {
                    startActivity(Intent(this, ReceiptActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    companion object {
        private const val TAG = "DetailActivity"
        private const val DATABASE_NAME = "destination"
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }
}