package com.example.traveleco

import com.example.traveleco.database.Destination
import com.google.gson.annotations.SerializedName

data class DestinationResponse(

    @field: SerializedName("error")
    val error: Boolean?,

    @field: SerializedName("message")
    val message: String?,

    @field: SerializedName("listStory")
    val destination: List<Destination>

)