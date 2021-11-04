package com.ivy.wallet.network.request.planned

import com.ivy.wallet.model.entity.PlannedPaymentRule


data class PlannedPaymentRulesResponse(
    val rules: List<PlannedPaymentRule>
)