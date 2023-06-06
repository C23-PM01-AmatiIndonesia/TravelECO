package com.example.traveleco.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.traveleco.database.Destination
import com.example.traveleco.databinding.ItemHomeBinding
import com.example.traveleco.ui.detail.DetailActivity

class DestinationAdapter(private val listDestination: ArrayList<Destination>) : RecyclerView.Adapter<DestinationAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val binding = ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyViewHolder(binding)
        }

        override fun getItemCount(): Int = listDestination.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                val destination = listDestination[position]

                holder.destinationName.text = destination.name
                holder.destinationLocation.text = destination.location
                holder.destinationDescription.text = destination.shortDescription
                Glide.with(holder.itemView)
                        .load(destination.photo_url)
                        .into(holder.destinationImage)
                holder.itemView.setOnClickListener {
                        val intent = Intent(holder.itemView.context, DetailActivity::class.java)
                        holder.itemView.context.startActivity(intent)
                }
        }

        class MyViewHolder(binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root) {
                val destinationName = binding.tvDestinationName
                val destinationLocation = binding.tvDestinationLocation
                val destinationDescription = binding.tvDestinationDescription
                val destinationImage = binding.ivDestination
        }
}