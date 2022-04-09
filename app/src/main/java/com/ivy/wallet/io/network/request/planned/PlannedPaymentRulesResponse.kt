package com.ivy.wallet.io.network.request.planned

import com.ivy.wallet.domain.data.entity.PlannedPaymentRule


data class PlannedPaymentRulesResponse(
    val rules: List<PlannedPaymentRule>
)