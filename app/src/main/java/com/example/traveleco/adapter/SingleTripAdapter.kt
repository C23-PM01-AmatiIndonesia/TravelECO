package com.example.traveleco.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.traveleco.database.ListBucket
import com.example.traveleco.database.SinglePrograms
import com.example.traveleco.databinding.ItemSingleBinding
import com.example.traveleco.ui.detail.DetailActivity
import com.example.traveleco.ui.detail.SingleActivity
import com.example.traveleco.ui.payment.PaymentMidtrans
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.Locale

class SingleTripAdapter(private val singleTrips: List<SinglePrograms>) : RecyclerView.Adapter<SingleTripAdapter.SingleProgramsViewHolder>() {

    private lateinit var database: DatabaseReference
    private lateinit var favoriteList: MutableList<ListBucket>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleProgramsViewHolder {
        val binding = ItemSingleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SingleProgramsViewHolder(binding)
    }

    override fun getItemCount(): Int = singleTrips.size

    override fun onBindViewHolder(holder: SingleProgramsViewHolder, position: Int) {
        val singleTrip = singleTrips[position]
        val priceToInt = singleTrip.nett_price
        val price = priceToInt?.toInt()
        val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedPrice = numberFormat.format(price)
        formattedPrice.toString()

        holder.tvProgram.text = singleTrip.activity_name
        holder.tvSubprogram.text = singleTrip.activity_type
        holder.tvDuration.text = singleTrip.duration
        holder.tvInclussion.text = singleTrip.inclussion
        holder.tvLevel.text = singleTrip.level
        holder.tvPriceSingle.text = "Rp $formattedPrice /Pack"
        Glide.with(holder.itemView).load(singleTrip.photo_url).into(holder.ivProgram)
        holder.btnViewPacket.setOnClickListener {
            val intent = Intent(holder.itemView.context, SingleActivity::class.java)
            intent.putExtra("MainProgram", singleTrip.activity_name)
            intent.putExtra("Photo", singleTrip.photo_url)
            intent.putExtra("Program", singleTrip.activity_type)
            intent.putExtra("Details", singleTrip.details)
            intent.putExtra("Duration", singleTrip.duration)
            intent.putExtra("Level", singleTrip.level)
            intent.putExtra("Inclussion", singleTrip.inclussion)
            intent.putExtra("Price", singleTrip.nett_price)
            holder.itemView.context.startActivity(intent)
        }
//        holder.btnOrder.setOnClickListener {
//            val intent = Intent(holder.itemView.context, PaymentMidtrans::class.java)
//            intent.putExtra("Price", singleTrip.nett_price)
//            intent.putExtra("Program", singleTrip.activity_type)
//            holder.itemView.context.startActivity(intent)
//        }
//        holder.btnAddToCart.setOnClickListener {
//            val packageName = singleTrip.activity_name.toString()
//            val packageDesc = singleTrip.inclussion.toString()
//            val packagePrice = singleTrip.nett_price.toString()
//            val packagePhoto = singleTrip.photo_url.toString()
//
//            database = FirebaseDatabase.getInstance().reference
//
//            // Simpan daftar favorit ke Firebase Database
//            val userId = FirebaseAuth.getInstance().currentUser?.uid
//            if (userId != null) {
//                val userFavoritesRef = database.child("users").child(userId).child("bucket")
//
//                // Membaca daftar favorit saat ini dari Firebase Database
//                userFavoritesRef.get().addOnSuccessListener { dataSnapshot ->
//                    val favoritesList = mutableListOf<Map<String, String>>()
//
//                    // Jika data "favorites" sudah ada, tambahkan ke dalam list
//                    if (dataSnapshot.exists()) {
//                        val currentFavorites = dataSnapshot.value as? List<Map<String, String>>
//                        currentFavorites?.let { favoritesList.addAll(it) }
//                    }
//
//                    // Buat objek favorit baru
//                    val favorite = mapOf(
//                        "packageDesc" to packageDesc,
//                        "packageName" to packageName,
//                        "packagePrice" to packagePrice,
//                        "photo_url" to packagePhoto
//                    )
//
//                    // Tambahkan objek favorit baru ke dalam list
//                    // Tambahkan objek favorit baru ke dalam list jika belum ada
//                    if (!favoritesList.any { it["packageName"] == packageName }) {
//                        favoritesList.add(favorite)
//
//                        // Simpan list favorit yang sudah diperbarui ke Firebase Database
//                        userFavoritesRef.setValue(favoritesList)
//                            .addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    Toast.makeText(holder.itemView.context, "Berhasil Menambahkan Paket ke Keranjang", Toast.LENGTH_SHORT).show()
//                                } else {
//                                    Toast.makeText(holder.itemView.context, "Gagal Menambahkan Paket ke Keranjang", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                    } else {
//                        Toast.makeText(holder.itemView.context, "Paket sudah ada pada keranjang", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
    }

    inner class SingleProgramsViewHolder(binding: ItemSingleBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvProgram = binding.tvProgram
        val tvSubprogram = binding.tvSubprogram
        val tvDuration = binding.tvDuration
        val tvInclussion = binding.tvInclussion
        val tvLevel = binding.tvLevel
        val tvPriceSingle = binding.tvPriceSingle
        val ivProgram = binding.ivProgram
        val btnViewPacket = binding.btnViewPacket
    }
}