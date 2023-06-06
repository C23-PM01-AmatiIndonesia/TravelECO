package com.example.traveleco.data

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class VaNumbersItem(

	@field:SerializedName("bank")
	val bank: String,

	@field:SerializedName("va_number")
	val vaNumber: String
) : Parcelable