package com.example.traveleco.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.traveleco.data.ApiConfig
import com.example.traveleco.data.CarbonResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CarbonViewModel : ViewModel() {

    private val _carbonEmission = MutableLiveData<CarbonResponse>()
    val carbonEmissionLiveData: LiveData<CarbonResponse> = _carbonEmission
    val errorLiveData: MutableLiveData<String> = MutableLiveData()

    fun getCarbonEmission(userCoordinate: String) {
        val client = ApiConfig.getApiCarbon().getPredictEmission(userCoordinate)
        client.enqueue(object : Callback<CarbonResponse> {
            override fun onResponse(call: Call<CarbonResponse>, response: Response<CarbonResponse>) {
                if (response.isSuccessful) {
                    _carbonEmission.value = response.body()
                } else {
                    val errorMessage = "Error: ${response.code()}"
                    errorLiveData.value = errorMessage
                }
            }
            override fun onFailure(call: Call<CarbonResponse>, t: Throwable) {
                errorLiveData.value = "Error: ${t.message}"
            }
        })
    }
}