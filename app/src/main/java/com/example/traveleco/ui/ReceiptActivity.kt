package com.example.traveleco.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.traveleco.MainActivity
import com.example.traveleco.R
import com.example.traveleco.database.Receipt
import com.example.traveleco.databinding.ActivityReceiptBinding
import com.example.traveleco.model.ReceiptViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ReceiptActivity : AppCompatActivity() {

    private var _binding: ActivityReceiptBinding? = null
    private val binding get() = _binding

    private lateinit var viewModel: ReceiptViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var orderList: ArrayList<Receipt>
    private lateinit var orderCurrency: String
    private lateinit var orderGetEmail: String
    private lateinit var orderGetId: String
    private lateinit var orderGetName: String
    private lateinit var orderGetOfPeople: String
    private lateinit var orderGetPaymentMerchant: String
    private lateinit var orderGetPhone: String
    private lateinit var orderGetPrice: String
    private lateinit var orderGetStatus: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val bottomNavigation = binding?.bottomNavigation
        @Suppress("DEPRECATION")
        bottomNavigation?.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottomNavigation?.selectedItemId = R.id.menu_receipt

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ReceiptViewModel::class.java]

        val sharedPref = getSharedPreferences("orderId", Context.MODE_PRIVATE)
        val orderId = sharedPref.getString("order_id", "")
        val orderName = sharedPref.getString("username_order", "")
        val orderEmail = sharedPref.getString("email_order", "")
        val orderPhone = sharedPref.getString("phone_number", "")
        val orderOfPerson = sharedPref.getString("number_of_person", "")

        viewModel.getOrderDetail(orderId!!)

        orderList = arrayListOf()

        viewModel.orderDetail.observe(this) { paymentResponse ->
            orderCurrency = paymentResponse.currency
            orderGetEmail = orderEmail!!
            orderGetId = paymentResponse.orderId
            orderGetName = orderName!!
            orderGetOfPeople = orderOfPerson!!
            orderGetPaymentMerchant = paymentResponse.paymentType
            orderGetPhone = orderPhone!!
            orderGetPrice = paymentResponse.grossAmount.toString()
            orderGetStatus = paymentResponse.transactionStatus
        }

//        val layoutManager = LinearLayoutManager(this)
//        binding?.rvReceipt?.layoutManager = layoutManager
//        binding?.rvReceipt?.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
        orderList = arrayListOf()
        getOrderData()
    }

    private fun getOrderData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userOrderRef = database.child("users").child(userId).child("order")

            userOrderRef.get().addOnSuccessListener { dataSnapshot ->
                val orderList = mutableListOf<Map<String, String?>>()
                // Jika data "favorites" sudah ada, tambahkan ke dalam list
                if (dataSnapshot.exists()) {
                    val currentFavorites = dataSnapshot.value as? List<Map<String, String>>
                    currentFavorites?.let { orderList.addAll(it) }

                    val order = mapOf(
                        "orderCurrency" to orderCurrency,
                        "orderEmail" to orderGetEmail,
                        "orderId" to orderGetId,
                        "orderName" to orderGetName,
                        "orderOfPeople" to orderGetOfPeople,
                        "orderPaymentMerchant" to orderGetPaymentMerchant,
                        "orderPhone" to orderGetPhone,
                        "orderPrice" to orderGetPrice,
                        "orderStatus" to orderGetStatus
                    )

                    // Tambahkan objek favorit baru ke dalam list
                    // Tambahkan objek favorit baru ke dalam list jika belum ada
                    if (!orderList.any { it["orderId"] == packageName }) {
                        orderList.add(order)

                        // Simpan list favorit yang sudah diperbarui ke Firebase Database
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

    @Suppress("DEPRECATION")
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_add_to_cart -> {
                    startActivity(Intent(this, BucketActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_receipt -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onBackPressed() {
        finishAffinity()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }
}