package com.ivy.exchange.fawazahmed0

import com.google.gson.annotations.SerializedName

data class Fawazahmed0Response(
    @SerializedName("date")
    val date: String,
    @SerializedName("eur")
    val eur: Map<String, Double>,
)