package com.example.traveleco.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.traveleco.database.ListBucket
import com.example.traveleco.databinding.ItemBucketBinding
import com.example.traveleco.ui.payment.PaymentMidtrans
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class BucketAdapter(private val listBucket: ArrayList<ListBucket>) : RecyclerView.Adapter<BucketAdapter.MyViewHolder>() {

    private lateinit var database: DatabaseReference
    private lateinit var favoriteList: MutableList<ListBucket>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemBucketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = listBucket.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val favorite = listBucket[position]
        holder.packageName.text = favorite.packageName
        holder.packageDescription.text = favorite.packageDesc
        holder.packagePrice.text = favorite.packagePrice
        Glide.with(holder.itemView)
            .load(favorite.photo_url)
            .into(holder.packageImage)
        holder.btnOrder.setOnClickListener {
            val intent = Intent(holder.itemView.context, PaymentMidtrans::class.java)
            intent.putExtra("Price", favorite.packagePrice)
            intent.putExtra("Program", favorite.packageName)
            holder.itemView.context.startActivity(intent)
        }
        holder.btnDeleteItem.setOnClickListener {
            // Hapus item dari tampilan
            val itemPosition = holder.adapterPosition
            val deletedItem = listBucket[itemPosition]
            listBucket.removeAt(itemPosition)
            notifyItemRemoved(itemPosition)

            // Hapus item dari data bucket di Firebase Realtime Database
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                database = FirebaseDatabase.getInstance().reference
                val userBucketRef = database.child("users").child(userId).child("bucket")

                userBucketRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val bucketList = snapshot.getValue<List<HashMap<String, Any>>>()
                            if (bucketList != null) {
                                // Cari item yang akan dihapus berdasarkan packageName
                                val updatedBucketList = bucketList.filterNot { item ->
                                    item["packageName"] == deletedItem.packageName
                                }

                                // Update data bucket di Firebase Realtime Database
                                userBucketRef.setValue(updatedBucketList)
                                    .addOnSuccessListener {
                                        Toast.makeText(holder.itemView.context, "Item dihapus dari favorit", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        // Jika gagal menghapus item, tambahkan kembali ke daftar favorit
                                        listBucket.add(itemPosition, deletedItem)
                                        notifyItemInserted(itemPosition)
                                        Toast.makeText(holder.itemView.context, "Gagal menghapus item dari favorit", Toast.LENGTH_SHORT).show()
                                    }

                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Jika terjadi kesalahan, tambahkan kembali ke daftar favorit
                        listBucket.add(itemPosition, deletedItem)
                        notifyItemInserted(itemPosition)
                        Toast.makeText(holder.itemView.context, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

    }

    class MyViewHolder(binding: ItemBucketBinding) : RecyclerView.ViewHolder(binding.root) {

        val packageName = binding.tvProgram
        val packageDescription = binding.tvDescription
        val packagePrice = binding.tvPriceOver
        val packageImage = binding.ivProgram
        val btnDeleteItem = binding.btnDeleteItem
        val btnOrder = binding.btnOrder
    }
}