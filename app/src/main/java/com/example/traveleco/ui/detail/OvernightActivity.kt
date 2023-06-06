package com.example.traveleco.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.traveleco.R
import com.example.traveleco.database.ComboPrograms
import com.example.traveleco.database.ListOverNight
import com.example.traveleco.databinding.ActivityComboBinding
import com.example.traveleco.databinding.ActivityOvernightBinding
import com.example.traveleco.ui.payment.PaymentMidtrans
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class OvernightActivity : AppCompatActivity() {

    private var _binding: ActivityOvernightBinding? = null
    private val binding get() = _binding
    private lateinit var auth: FirebaseAuth
    private lateinit var overNightTrips: ArrayList<ListOverNight>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOvernightBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val program = intent.getStringExtra("Program")
        val price = intent.getStringExtra("Price")

        binding?.btnOrder?.setOnClickListener {
            val intent = Intent(this, PaymentMidtrans::class.java)
            intent.putExtra("Program", program)
            intent.putExtra("Price", price)
            startActivity(intent)
        }
        binding?.btnAddToCart?.setOnClickListener {
            val packageName = intent.getStringExtra("Program")
            val packageDesc = intent.getStringExtra("Description")
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
        overNightTrips = arrayListOf()
        getOverNightData()
    }

    @SuppressLint("SetTextI18n")
    private fun getOverNightData() {
        val program = intent.getStringExtra("Program")
        val photo = intent.getStringExtra("Photo")
        val duration = intent.getStringExtra("Duration")
        val desc = intent.getStringExtra("Description")
        val price = intent.getStringExtra("Price")
        val priceToInt = price?.toInt()
        val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedPrice = numberFormat.format(priceToInt)
        formattedPrice.toString()


        binding?.tvProgram?.text = program
        Glide.with(applicationContext)
            .load(photo)
            .into(binding!!.ivProgram)
        binding?.tvDuration?.text = duration
        binding?.tvDescription?.text = desc
        binding?.tvPriceOver?.text = "Rp $formattedPrice /Pack/Nett"
    }
}