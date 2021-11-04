package com.ivy.wallet.network.request.bankintegrations

import com.google.gson.annotations.SerializedName
import com.ivy.wallet.model.bankintegrations.SEConnection

data class BankConnectionsResponse(
    @SerializedName("connections")
    val connections: List<SEConnection>
)