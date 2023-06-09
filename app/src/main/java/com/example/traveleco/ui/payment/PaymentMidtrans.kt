package com.example.traveleco.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.traveleco.MainActivity
import com.example.traveleco.database.Receipt
import com.example.traveleco.databinding.ActivityOrderBinding
import com.example.traveleco.model.ReceiptViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.uikit.SdkUIFlowBuilder


class PaymentMidtrans : AppCompatActivity() {

    private var _binding: ActivityOrderBinding? = null
    private val binding get() = _binding

    private lateinit var viewModel: ReceiptViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userName : String
    private lateinit var userEmail : String
    private lateinit var phoneNumber : String
    private lateinit var userCountry: String
    private lateinit var person: String
    private lateinit var orderId: String
    private lateinit var orderList: ArrayList<Receipt>
    private lateinit var orderCurrency: String
    private lateinit var orderGetPaymentMerchant: String
    private lateinit var orderGetPrice: String
    private lateinit var orderGetStatus: String
    private lateinit var orderGetId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ReceiptViewModel::class.java]

        orderList = arrayListOf()

        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-EV6tyZrK3OzoMgJ3") // client_key is mandatory
            .setContext(applicationContext) // context is mandatory
            .setTransactionFinishedCallback {
                getOrderData()
            } // set transaction finish callback (sdk callback)
            .setMerchantBaseUrl("https://midtrans-api-jl4gfhbdkq-as.a.run.app/index.php/") //set merchant url (required)
            .enableLog(true) // enable sdk log (optional)
            .setColorTheme(
                CustomColorTheme(
                    "#7ADE7A",
                    "#1E641E",
                    "#37FF37"
                )
            ) // set theme. it will replace theme on snap theme on MAP ( optional)
            .setLanguage("en") //`en` for English and `id` for Bahasa
            .buildSDK()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val displayName = sharedPref.getString("displayName", "")
        val email = sharedPref.getString("email", "")

        userName = displayName!!
        userEmail = email!!
        binding?.etName?.setText(userName)
        binding?.etEmail?.setText(userEmail)
        binding?.etPerson?.setText("2")

        val nameOrder = intent.getStringExtra("Program")
        val imageOrder = intent.getStringExtra("Photo")

        binding?.tvProgram?.text = nameOrder
        Glide.with(this).load(imageOrder).into(binding!!.ivPreview)

        binding?.btnSend?.setOnClickListener {
            payment()
        }
    }

    private fun payment() {
        val price = intent.getStringExtra("Price")
        val convertPrice = price?.toDouble()
        person = binding?.etPerson?.text.toString()
        val convertPerson = person.toInt()
        val times = convertPrice?.times(convertPerson)

        userName = binding?.etName?.text.toString()
        userEmail = binding?.etEmail?.text.toString()
        userCountry = binding?.etCountry?.text.toString()
        phoneNumber = binding?.etPhone?.text.toString()

        orderId = "TravelECO-"+System.currentTimeMillis().toString() + ""

        val nameProgram = intent.getStringExtra("Program")
        val transactionRequest = TransactionRequest(orderId, times!!)
        val detail = com.midtrans.sdk.corekit.models.ItemDetails(System.currentTimeMillis().toString(), convertPrice, convertPerson, nameProgram)
        val itemDetails = ArrayList<com.midtrans.sdk.corekit.models.ItemDetails>()
        itemDetails.add(detail)
        uiKitDetails(transactionRequest, userName, userEmail, phoneNumber)
        transactionRequest.itemDetails = itemDetails
        MidtransSDK.getInstance().transactionRequest = transactionRequest
        MidtransSDK.getInstance().startPaymentUiFlow(this)
    }

    private fun getOrderData() {
        viewModel.getOrderDetail(orderId)
        viewModel.orderDetail.observe(this) { paymentResponse ->
            orderCurrency = paymentResponse.currency
            orderGetId = paymentResponse.orderId
            orderGetPaymentMerchant = paymentResponse.paymentType
            orderGetPrice = paymentResponse.grossAmount.toString()
            orderGetStatus = paymentResponse.transactionStatus
        }
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userOrderRef = database.child(userId).child("order")
            userOrderRef.get().addOnSuccessListener { dataSnapshot ->
                val orderList = mutableListOf<Map<String, String?>>()
                if (dataSnapshot.exists()) {
                    val currentOrder = dataSnapshot.value as? List<Map<String, String>>
                    currentOrder?.let { orderList.addAll(it) }

                    val order = mapOf(
                        "orderCurrency" to orderCurrency,
                        "orderEmail" to userEmail,
                        "orderId" to orderGetId,
                        "orderName" to userName,
                        "orderOfPeople" to person,
                        "orderPaymentMerchant" to orderGetPaymentMerchant,
                        "orderPhone" to phoneNumber,
                        "orderPrice" to orderGetPrice,
                        "orderStatus" to orderGetStatus
                    )

                    if (!orderList.any { it["orderId"] == orderId }) {
                        orderList.add(order)

                        userOrderRef.setValue(orderList)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Successfully Add Package to Order List", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Failed to Add Package to Order List", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Failed to Add Package to Order List", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun uiKitDetails(
        transactionRequest: TransactionRequest,
        name: String,
        email: String,
        phone: String)
    {
        val customerDetails = CustomerDetails()
        customerDetails.firstName = name
        customerDetails.email = email
        customerDetails.phone = phone
        transactionRequest.customerDetails = customerDetails
    }
}