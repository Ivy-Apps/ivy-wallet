package com.ivy.wallet.domain.data.bankintegrations

import com.google.gson.annotations.SerializedName

data class SETransaction(
    @SerializedName("id")
    val id: String,

    @SerializedName("duplicated")
    val duplicated: Boolean,

    @SerializedName("mode")
    val mode: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("made_on")
    val made_on: String,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("currency_code")
    val currency_code: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("account_id")
    val account_id: String,

    @SerializedName("created_at")
    val created_at: String,

    @SerializedName("updated_at")
    val updated_at: String,

    @SerializedName("extra")
    val extra: Map<String, Any>?
)