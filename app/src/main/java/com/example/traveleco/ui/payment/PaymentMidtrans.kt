package com.example.traveleco.ui.payment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.traveleco.database.Users
import com.example.traveleco.databinding.ActivityOrderBinding
import com.example.traveleco.ui.auth.activity.LoginActivity
import com.google.android.gms.common.internal.service.Common.CLIENT_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-EV6tyZrK3OzoMgJ3") // client_key is mandatory
            .setContext(applicationContext) // context is mandatory
            .setTransactionFinishedCallback {
                // Handle finished transaction here.
            } // set transaction finish callback (sdk callback)
            .setMerchantBaseUrl("https://midtrans-api-jl4gfhbdkq-as.a.run.app/index.php/") //set merchant url (required)
            .enableLog(true) // enable sdk log (optional)
            .setColorTheme(
                CustomColorTheme(
                    "#FFE51255",
                    "#B61548",
                    "#FFE51255"
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

//        isFromLogin = intent.getBooleanExtra(LoginActivity.FROM_LOGIN, true)
//        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
//        val displayName = sharedPref.getString("displayName", "")
//        val email = sharedPref.getString("email", "")
//
//        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
//
//        if (isFromLogin) {
//            userName = displayName!!
//            userEmail = email!!
//            binding?.etName?.setText(displayName)
//            binding?.etEmail?.setText(email)
//        }
//        database.child(currentUserUid!!).addListenerForSingleValueEvent(object :
//            ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    val user = snapshot.getValue(Users::class.java)
//                    userName = user?.name!!
//                    userEmail = user.email!!
//                    binding?.etName?.setText(user.name)
//                    binding?.etEmail?.setText(user.email)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("ProfileActivity", "Gagal")
//            }
//        })

        val nameOrder = intent.getStringExtra("Program")
        val imageOrder = intent.getStringExtra("Photo")

        binding?.tvProgram?.text = nameOrder
        Glide.with(this).load(imageOrder).into(binding!!.ivPreview)

        binding?.btnSend?.setOnClickListener {
            payment()
        }

        phoneNumber = binding?.etPhone?.text.toString()
    }

    private fun payment() {
        val price = intent.getStringExtra("Price")
        val convertPrice = price?.toDouble()
        val person = binding?.etPerson?.text.toString()
        val convertPerson = person.toInt()
        val times = convertPrice?.times(convertPerson)

        userName = binding?.etName?.text.toString()
        userEmail = binding?.etEmail?.text.toString()
        userCountry = binding?.etCountry?.text.toString()
        phoneNumber = binding?.etPhone?.text.toString()

        val orderId = "TravelECO-"+System.currentTimeMillis().toString() + ""

        val nameProgram = intent.getStringExtra("Program")
        val transactionRequest = TransactionRequest(orderId, times!!)
        val detail = com.midtrans.sdk.corekit.models.ItemDetails(System.currentTimeMillis().toString(), convertPrice, convertPerson, nameProgram)
        val itemDetails = ArrayList<com.midtrans.sdk.corekit.models.ItemDetails>()
        itemDetails.add(detail)
        uiKitDetails(transactionRequest, userName, userEmail, phoneNumber)
        transactionRequest.itemDetails = itemDetails
        MidtransSDK.getInstance().transactionRequest = transactionRequest
        MidtransSDK.getInstance().startPaymentUiFlow(this)
        val sharedPref = getSharedPreferences("order", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("order_id", orderId)
        editor.putString("name_program", nameProgram)
        editor.putString("username_order", userName)
        editor.putString("email_order", userEmail)
        editor.putString("phone_number", phoneNumber)
        editor.putString("number_of_person", person)
        editor.apply()
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