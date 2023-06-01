package com.example.traveleco

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.traveleco.database.Destination
import com.example.traveleco.databinding.ItemHomeBinding
import com.example.traveleco.ui.detail.DetailActivity

class DestinationAdapter(private val listDestination: ArrayList<Destination>) :
        RecyclerView.Adapter<DestinationAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val binding =
                        ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MyViewHolder(binding)
        }

        override fun getItemCount(): Int = listDestination.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
                val destination = listDestination[position]
                holder.destinationName.text = destination.name
                holder.destinationLocation.text = destination.location
                holder.destinationDescription.text = destination.shortDescription
                Glide.with(holder.itemView)
                        .load(destination.photoUrl)
                        .into(holder.destinationImage)

                holder.itemView.setOnClickListener {
                        val intent = Intent(holder.itemView.context, DetailActivity::class.java)
                        holder.itemView.context.startActivity(intent)
                }
        }

        class MyViewHolder(binding: ItemHomeBinding) :
                RecyclerView.ViewHolder(binding.root) {

                val destinationName = binding.tvDestinationName
                val destinationLocation = binding.tvDestinationLocation
                val destinationDescription = binding.tvDestinationDescription
                val destinationImage = binding.ivDestination

//                tvDestinationName.text = destination.name
//                tvDestinationLocation.text = destination.location
//                tvDestinationDescription.text = destination.description
//                Glide.with(itemView)
//                .load(destination.photoUrl)
//                .into(ivDestination)
//                itemView.setOnClickListener {
//                        val intentDetail =
//                                Intent(itemView.context, DetailActivity::class.java)
//                        intentDetail.putExtra(DetailActivity.DESTINATION_NAME, destination.name)
//                        intentDetail.putExtra(DetailActivity.DESTINATION_LOCATION, destination.location)
//                        intentDetail.putExtra(
//                                DetailActivity.DESTINATION_DESCRIPTION,
//                                destination.description
//                        )
//                        itemView.context.startActivity(intentDetail)
//                }
        }
}