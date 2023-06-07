package com.example.traveleco.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.ViewModelProvider
import com.example.traveleco.MainActivity
import com.example.traveleco.R
import com.example.traveleco.data.PaymentResponse
import com.example.traveleco.databinding.ActivityReceiptBinding
import com.example.traveleco.databinding.ItemReceiptBinding
import com.example.traveleco.model.PaymentViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class ReceiptActivity : AppCompatActivity() {
    private var _binding: ItemReceiptBinding? = null
    private val binding get() = _binding
    private lateinit var viewModel: PaymentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ItemReceiptBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

//        val bottomNavigation = binding?.bottomNavigation
//        @Suppress("DEPRECATION")
//        bottomNavigation?.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
//        bottomNavigation?.selectedItemId = R.id.menu_receipt

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[PaymentViewModel::class.java]

//        val sharedPref = getSharedPreferences("orderId", Context.MODE_PRIVATE)
//        val orderId = sharedPref.getString("order_id", "")

        val orderId = "TravelECO-1686057678054"
        // Ubah sesuai dengan orderId yang ingin Anda ambil detailnya
        viewModel.getOrderDetail(orderId)

        viewModel.orderDetail.observe(this) { paymentResponse ->
            // Lakukan sesuatu dengan data response
            binding?.apply {
                tvOrderId.text = paymentResponse.orderId
                tvStatus.text = paymentResponse.transactionStatus
                tvCurrency.text = paymentResponse.currency
                tvPrice.text = paymentResponse.grossAmount.toString()
                tvPaymentMerchant.text = paymentResponse.paymentType
            }
        }

    }
//
//    @Suppress("DEPRECATION")
//    private val onNavigationItemSelectedListener =
//        BottomNavigationView.OnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.menu_home -> {
//                    startActivity(Intent(this, MainActivity::class.java))
//                    return@OnNavigationItemSelectedListener true
//                }
//                R.id.menu_add_to_cart -> {
//                    startActivity(Intent(this, BucketActivity::class.java))
//                    return@OnNavigationItemSelectedListener true
//                }
//                R.id.menu_receipt -> {
//                    return@OnNavigationItemSelectedListener true
//                }
//                R.id.menu_profile -> {
//                    startActivity(Intent(this, ProfileActivity::class.java))
//                    return@OnNavigationItemSelectedListener true
//                }
//            }
//            false
//        }

//    override fun onBackPressed() {
//        finishAffinity()
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.main_menu, menu)
//        return true
//    }
}