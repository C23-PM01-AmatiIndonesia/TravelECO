package com.example.traveleco.model

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.traveleco.data.ApiConfig
import com.example.traveleco.data.PaymentResponse
import com.example.traveleco.data.VaNumbersItem
import com.example.traveleco.ui.ReceiptActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentViewModel() : ViewModel() {


    private val _orderDetail = MutableLiveData<List<PaymentResponse>>()
    val orderDetail: LiveData<List<PaymentResponse>> = _orderDetail

    private val _detailMerchant = MutableLiveData<List<VaNumbersItem>>()
    val detailMerchant: LiveData<List<VaNumbersItem>> = _detailMerchant

    fun getOrderDetail(orderId: String = "") {
        val client = ApiConfig.getApiService().getOrderDetail(orderId)
        client.enqueue(object : Callback<List<PaymentResponse>> {
            override fun onResponse(
                call: Call<List<PaymentResponse>>,
                response: Response<List<PaymentResponse>>
            ) {
                if (response.isSuccessful) {
                    _orderDetail.value = response.body()
                } else {
                    Log.d("DetailView", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<PaymentResponse>>, t: Throwable) {
                Log.e("DetailView", "onFailure: ${t.message}")
                Toast.makeText(ReceiptActivity(), "Sorry, Can't Get Followers Detail", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun getMerchant(orderId: String = "") {
        val client = ApiConfig.getApiService().getMerchant(orderId)
        client.enqueue(object : Callback<List<VaNumbersItem>> {
            override fun onResponse(
                call: Call<List<VaNumbersItem>>,
                response: Response<List<VaNumbersItem>>
            ) {
                if (response.isSuccessful) {
                    _detailMerchant.value = response.body()
                } else {
                    Log.d("DetailView", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<VaNumbersItem>>, t: Throwable) {
                Log.e("DetailView", "onFailure: ${t.message}")
                Toast.makeText(ReceiptActivity(), "Sorry, Can't Get Followers Detail", Toast.LENGTH_SHORT).show()
            }
        })
    }
}