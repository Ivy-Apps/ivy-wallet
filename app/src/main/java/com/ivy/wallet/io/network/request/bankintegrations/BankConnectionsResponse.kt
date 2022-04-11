package com.ivy.wallet.io.network.request.bankintegrations

import com.google.gson.annotations.SerializedName
import com.ivy.wallet.domain.data.bankintegrations.SEConnection

data class BankConnectionsResponse(
    @SerializedName("connections")
    val connections: List<SEConnection>
)