package com.example.traveleco.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.traveleco.MainActivity
import com.example.traveleco.R
import com.example.traveleco.adapter.DestinationAdapter
import com.example.traveleco.database.Destination
import com.example.traveleco.database.SinglePrograms
import com.example.traveleco.databinding.ActivityDetailBinding
import com.example.traveleco.databinding.ActivityMainBinding
import com.example.traveleco.databinding.ActivitySingleBinding
import com.example.traveleco.ui.maps.MapsActivity
import com.example.traveleco.ui.payment.PaymentMidtrans
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class SingleActivity : AppCompatActivity() {

    private var _binding: ActivitySingleBinding? = null
    private val binding get() = _binding
    private lateinit var auth: FirebaseAuth
    private lateinit var singleTrips: ArrayList<SinglePrograms>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySingleBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val program = intent.getStringExtra("SubProgram")
        val price = intent.getStringExtra("Price")
        val photo = intent.getStringExtra("Photo")

        binding?.btnOrder?.setOnClickListener {
            val intent = Intent(this, PaymentMidtrans::class.java)
            intent.putExtra("SubProgram", program)
            intent.putExtra("Price", price)
            intent.putExtra("Photo", photo)
            startActivity(intent)
        }

        binding?.btnAddToCart?.setOnClickListener {
            val packageName = intent.getStringExtra("Program")
            val packageDesc = intent.getStringExtra("Inclussion")
            val packagePrice = intent.getStringExtra("Price")
            val packagePhoto = intent.getStringExtra("Photo")

            database = FirebaseDatabase.getInstance().reference

            // Simpan daftar favorit ke Firebase Database
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val userFavoritesRef = database.child("users").child(userId).child("bucket")

                // Membaca daftar favorit saat ini dari Firebase Database
                userFavoritesRef.get().addOnSuccessListener { dataSnapshot ->
                    val favoritesList = mutableListOf<Map<String, String?>>()

                    // Jika data "favorites" sudah ada, tambahkan ke dalam list
                    if (dataSnapshot.exists()) {
                        val currentFavorites = dataSnapshot.value as? List<Map<String, String>>
                        currentFavorites?.let { favoritesList.addAll(it) }
                    }

                    // Buat objek favorit baru
                    val favorite = mapOf(
                        "packageDesc" to packageDesc,
                        "packageName" to packageName,
                        "packagePrice" to packagePrice,
                        "photo_url" to packagePhoto
                    )

                    // Tambahkan objek favorit baru ke dalam list
                    // Tambahkan objek favorit baru ke dalam list jika belum ada
                    if (!favoritesList.any { it["packageName"] == packageName }) {
                        favoritesList.add(favorite)

                        // Simpan list favorit yang sudah diperbarui ke Firebase Database
                        userFavoritesRef.setValue(favoritesList)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Berhasil Menambahkan Paket ke Keranjang", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Gagal Menambahkan Paket ke Keranjang", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Paket sudah ada pada keranjang", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        auth = FirebaseAuth.getInstance()
        singleTrips = arrayListOf()
        getSingleData()
    }

    @SuppressLint("SetTextI18n")
    private fun getSingleData() {
        val program = intent.getStringExtra("Program")
        val photo = intent.getStringExtra("Photo")
        val subProgram = intent.getStringExtra("SubProgram")
        val details = intent.getStringExtra("Details")
        val duration = intent.getStringExtra("Duration")
        val level = intent.getStringExtra("Level")
        val inclussion = intent.getStringExtra("Inclussion")
        val price = intent.getStringExtra("Price")
        val priceToInt = price?.toInt()
        val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedPrice = numberFormat.format(priceToInt)
        formattedPrice.toString()

        binding?.tvProgram?.text = program
        Glide.with(applicationContext)
            .load(photo)
            .into(binding!!.ivProgram)
        binding?.tvSubprogram?.text = subProgram
        binding?.tvDetail?.text = details
        binding?.tvDuration?.text = duration
        binding?.tvLevel?.text = level
        binding?.tvInclussion?.text = inclussion
        binding?.tvPriceSingle?.text = "Rp $formattedPrice /Pack"

    }

}