package com.example.traveleco.ui.packet

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.traveleco.R
import com.example.traveleco.databinding.ActivityPackageBinding

class PackageActivity : AppCompatActivity() {

    private var _binding: ActivityPackageBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPackageBinding.inflate(layoutInflater)
        setContentView(binding?.root)


    }


}