package com.example.traveleco.ui.payment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.traveleco.database.Users
import com.example.traveleco.databinding.ActivityOrderBinding
import com.example.traveleco.model.AuthViewModel
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
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ShippingAddress


class PaymentMidtrans : AppCompatActivity() {
    private var _binding: ActivityOrderBinding? = null
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var authViewModel: AuthViewModel
    private var isFromLogin: Boolean = false
    private lateinit var name : String
    private lateinit var getEmail : String
    private lateinit var phone : String
    private var fixPrice : Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        isFromLogin = intent.getBooleanExtra(LoginActivity.FROM_LOGIN, true)
        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val displayName = sharedPref.getString("displayName", "")
        val email = sharedPref.getString("email", "")

        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        if (isFromLogin) {
            name = displayName
            getEmail = email
        }
        database.child(currentUserUid!!).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(Users::class.java)

                    binding?.tvUser?.text = user?.name
                    binding?.tvEmail?.text = user?.email
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ProfileActivity", "Gagal")
            }
        })

        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-EV6tyZrK3OzoMgJ3") // client_key is mandatory
            .setContext(applicationContext) // context is mandatory
            .setTransactionFinishedCallback {
                // Handle finished transaction here.
            } // set transaction finish callback (sdk callback)
            .setMerchantBaseUrl("http://travelecoapp.infinityfreeapp.com/index.php/charge") //set merchant url (required)
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

        binding?.btnSend?.setOnClickListener {
            payment()
        }

    }

    private fun payment() {
        val price = intent.getStringExtra("Price")
        val convertPrice = price?.toDouble()
        val person = binding?.etPerson?.text.toString()
        val convertPerson = person.toInt()
        val times = convertPrice?.times(convertPerson)
        fixPrice = times

        val nameProgram = intent.getStringExtra("Program")
        val transactionRequest = TransactionRequest("TravelECO-"+System.currentTimeMillis().toString() + "", fixPrice!!)
        val detail = com.midtrans.sdk.corekit.models.ItemDetails(nameProgram, convertPrice, convertPerson, "TravelECO")
        val itemDetails = ArrayList<com.midtrans.sdk.corekit.models.ItemDetails>()
        itemDetails.add(detail)
        uiKitDetails(transactionRequest)
        transactionRequest.itemDetails = itemDetails
        MidtransSDK.getInstance().transactionRequest = transactionRequest
        MidtransSDK.getInstance().startPaymentUiFlow(this)
    }

    fun uiKitDetails(transactionRequest: TransactionRequest){

        val customerDetails = CustomerDetails()
        customerDetails.customerIdentifier = "Supriyanto"
        customerDetails.phone = "082345678999"
        customerDetails.email = "supriyanto6543@gmail.com"

        transactionRequest.customerDetails = customerDetails
    }

}