package com.example.traveleco.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.traveleco.database.SinglePrograms
import com.example.traveleco.databinding.ItemSingleBinding

class SingleTripAdapter(private val singleTrips: List<SinglePrograms>) : RecyclerView.Adapter<SingleTripAdapter.SingleProgramsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleProgramsViewHolder {
        val binding = ItemSingleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SingleProgramsViewHolder(binding)
    }

    override fun getItemCount(): Int = singleTrips.size

    override fun onBindViewHolder(holder: SingleProgramsViewHolder, position: Int) {
        val singleTrip = singleTrips[position]

        holder.tvProgram.text = singleTrip.activity_name
        holder.tvSubprogram.text = singleTrip.activity_type
        holder.tvDetail.text = singleTrip.details
        holder.tvDuration.text = singleTrip.duration
        holder.tvInclussion.text = singleTrip.inclussion
        holder.tvLevel.text = singleTrip.level
        holder.tvPriceSingle.text = singleTrip.nett_price
        Glide.with(holder.itemView).load(singleTrip.photo_url).into(holder.ivProgram)
    }

    inner class SingleProgramsViewHolder(private val binding: ItemSingleBinding) : RecyclerView.ViewHolder(binding.root) {

        val tvProgram = binding.tvProgram
        val tvSubprogram = binding.tvSubprogram
        val tvDetail = binding.tvDetail
        val tvDuration = binding.tvDuration
        val tvInclussion = binding.tvInclussion
        val tvLevel = binding.tvLevel
        val tvPriceSingle = binding.tvPriceSingle
        val ivProgram = binding.ivProgram
    }
}