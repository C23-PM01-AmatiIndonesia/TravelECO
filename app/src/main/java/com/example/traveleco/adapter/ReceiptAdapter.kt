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
        holder.orderCurrency.text = receipt.orderName
        holder.orderPhone.text = receipt.orderEmail
        holder.orderOfPeople.text = receipt.orderId
    }

    class MyViewHolder(binding: ItemReceiptBinding) : RecyclerView.ViewHolder(binding.root) {
        val orderCurrency = binding.tvCurrency
        val orderEmail = binding.tvEmailReceipt
        val orderId = binding.tvOrderId
        val orderName = binding.tvNameReceipt
        val orderOfPeople = binding.tvNumReceipt
        val orderPaymentMerchant = binding.tvPaymentMerchant
        val orderPhone = binding.tvPhoneReceipt
        val orderPrice = binding.tvPrice
        val orderStatus = binding.tvStatus
    }
}