package com.example.traveleco.database

data class Receipt(
    val orderCurrency: String? = null,
    val orderEmail: String? = null,
    val orderId: String? = null,
    val orderName: String? = null,
    val orderOfPeople: String? = null,
    val orderPaymentMerchant: String? = null,
    val orderPhone: String? = null,
    val orderPrice: String? = null,
    val orderStatus: String? = null
)
