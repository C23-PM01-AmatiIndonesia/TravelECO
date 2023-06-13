package com.example.traveleco.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.traveleco.database.Receipt
import com.example.traveleco.databinding.ItemReceiptBinding

class ReceiptAdapter(private val listReceipt: ArrayList<Receipt>) : RecyclerView.Adapter<ReceiptAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemReceiptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = listReceipt.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val receipt = listReceipt[position]

        holder.orderName.text = receipt.orderName
        holder.orderEmail.text = receipt.orderEmail
        holder.orderId.text = receipt.orderId
        holder.orderPhone.text = receipt.orderEmail
        holder.orderProgram.text = receipt.orderProgram
        holder.orderOfPeople.text = receipt.orderOfPeople
        holder.orderPhone.text = receipt.orderPhone
        holder.orderPrice.text = receipt.orderPrice.toString()
    }

    class MyViewHolder(binding: ItemReceiptBinding) : RecyclerView.ViewHolder(binding.root) {
        val orderEmail = binding.tvEmailReceipt
        val orderId = binding.tvOrderId
        val orderName = binding.tvNameReceipt
        val orderProgram = binding.tvProgReceipt
        val orderOfPeople = binding.tvNumReceipt
        val orderPhone = binding.tvPhoneReceipt
        val orderPrice = binding.tvPrice
    }
}