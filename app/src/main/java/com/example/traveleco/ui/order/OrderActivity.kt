package com.example.traveleco.ui.order

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.traveleco.R
import com.example.traveleco.databinding.ActivityOrderBinding

class OrderActivity : AppCompatActivity() {

    lateinit var binding: ActivityOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSend.setOnClickListener{

            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val country = binding.etCountry.text.toString()
            val number = binding.etPhone.text.toString()
            val person = binding.etPerson.text.toString()

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, "dedenindo1412@gmail.com".split(",".toRegex()).toTypedArray())
                putExtra(Intent.EXTRA_SUBJECT, "Daftar Pemesanan Baru")
                putExtra(Intent.EXTRA_TEXT,
                "Nama          : $name \n" +
                        "Email        : $email \n" +
                        "Kebangsaan : $country \n" +
                        "No. WA      : $number \n" +
                        "Pengunjung : $person Orang \n")
            }

            if (intent.resolveActivity(packageManager) != null){
                startActivity(intent)
            } else{
                Toast.makeText(this, "Required App is not installed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}