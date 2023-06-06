package com.example.traveleco.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.traveleco.database.ComboPrograms
import com.example.traveleco.database.ListBucket
import com.example.traveleco.databinding.ItemComboBinding
import com.example.traveleco.ui.detail.ComboActivity
import com.example.traveleco.ui.detail.SingleActivity
import com.example.traveleco.ui.payment.PaymentMidtrans
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.Locale

class ComboTripAdapter(private val comboTrips: List<ComboPrograms>) : RecyclerView.Adapter<ComboTripAdapter.ComboProgramsViewHolder>() {

    private lateinit var database: DatabaseReference
    private lateinit var favoriteList: MutableList<ListBucket>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComboProgramsViewHolder {
        val binding = ItemComboBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ComboProgramsViewHolder(binding)
    }

    override fun getItemCount(): Int = comboTrips.size

    override fun onBindViewHolder(holder: ComboProgramsViewHolder, position: Int) {
        val comboTrip = comboTrips[position]
        val priceToInt = comboTrip.nett_price
        val price = priceToInt?.toInt()
        val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedPrice = numberFormat.format(price)
        formattedPrice.toString()

        holder.tvProgram.text = comboTrip.activity_name
        holder.tvDuration.text = comboTrip.duration
        holder.tvInclussion.text = comboTrip.inclussion
        holder.tvItem.text = comboTrip.start_time
        holder.priceCombo.text = "Rp $formattedPrice"
        Glide.with(holder.itemView).load(comboTrip.photo_url).into(holder.ivProgram)
        holder.btnViewPacket.setOnClickListener {
            val intent = Intent(holder.itemView.context, ComboActivity::class.java)
            intent.putExtra("Program", comboTrip.activity_name)
            intent.putExtra("Photo", comboTrip.photo_url)
            intent.putExtra("Duration", comboTrip.duration)
            intent.putExtra("Inclussion", comboTrip.inclussion)
            intent.putExtra("Time", comboTrip.start_time)
            intent.putExtra("Price", comboTrip.nett_price)
            holder.itemView.context.startActivity(intent)
        }
//        holder.btnOrder.setOnClickListener {
//            val intent = Intent(holder.itemView.context, PaymentMidtrans::class.java)
//            intent.putExtra("Price", comboTrip.nett_price)
//            intent.putExtra("Program", comboTrip.activity_name)
//            holder.itemView.context.startActivity(intent)
//        }
//        holder.btnAddToCart.setOnClickListener {
//            val packageName = comboTrip.activity_name.toString()
//            val packageDesc = comboTrip.inclussion.toString()
//            val packagePrice = comboTrip.nett_price.toString()
//            val packagePhoto = comboTrip.photo_url.toString()
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

    inner class ComboProgramsViewHolder(binding: ItemComboBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvProgram = binding.tvProgram
        val tvDuration = binding.tvDuration
        val tvInclussion = binding.tvInclussion
        val tvItem = binding.tvTime
        val priceCombo = binding.tvPriceCombo
        val ivProgram = binding.ivProgram1
        val btnViewPacket = binding.btnViewPacket
    }
}