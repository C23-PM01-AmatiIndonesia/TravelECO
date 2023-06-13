package com.example.traveleco.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.traveleco.database.Receipt
import com.example.traveleco.databinding.ActivityOrderBinding
import com.example.traveleco.ui.ReceiptActivity
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

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userName : String
    private lateinit var userEmail : String
    private lateinit var phoneNumber : String
    private lateinit var userCountry: String
    private lateinit var person: String
    private lateinit var orderId: String
    private lateinit var orderList: ArrayList<Receipt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        orderList = arrayListOf()

        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-EV6tyZrK3OzoMgJ3") // client_key is mandatory
            .setContext(applicationContext) // context is mandatory
            .setTransactionFinishedCallback {

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
            database = FirebaseDatabase.getInstance().reference

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

            val sharedPrefs = getSharedPreferences("orderId", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putString("orderId", orderId)
            editor.apply()

            val nameProgram = intent.getStringExtra("Program")
            val transactionRequest = TransactionRequest(orderId, times!!)
            val detail = com.midtrans.sdk.corekit.models.ItemDetails(System.currentTimeMillis().toString(), convertPrice, convertPerson, nameProgram)
            val itemDetails = ArrayList<com.midtrans.sdk.corekit.models.ItemDetails>()
            itemDetails.add(detail)
            uiKitDetails(transactionRequest, userName, userEmail, phoneNumber)
            transactionRequest.itemDetails = itemDetails
            MidtransSDK.getInstance().transactionRequest = transactionRequest
            MidtransSDK.getInstance().startPaymentUiFlow(this)

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val userOrderRef = database.child("users").child(userId).child("order")
                userOrderRef.get().addOnSuccessListener { dataSnapshot ->
                    val orderList = mutableListOf<Map<String, String?>>()
                    if (dataSnapshot.exists()) {
                        val currentFavorites = dataSnapshot.value as? List<Map<String, String>>
                        currentFavorites?.let { orderList.addAll(it) }
                    }

                    val order = mapOf(
                        "orderEmail" to userEmail,
                        "orderId" to orderId,
                        "orderName" to userName,
                        "orderProgram" to nameProgram,
                        "orderOfPeople" to person,
                        "orderPhone" to phoneNumber,
                        "orderPrice" to times.toString()
                    )

                    if (!orderList.any { it["orderId"] == orderId }) {
                        orderList.add(order)

                        userOrderRef.setValue(orderList)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("PaymentActivity", "Successfully added to the database")
                                } else {
                                    Log.d("PaymentActivity", "Failed to add to database")
                                }
                            }

                    } else {
                        Log.d("PaymentActivity", "Data already exists in the database")
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, ReceiptActivity::class.java)
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