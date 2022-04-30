package com.ivy.wallet.io.network.request.planned

import com.ivy.wallet.io.network.data.PlannedPaymentRuleDTO


data class PlannedPaymentRulesResponse(
    val rules: List<PlannedPaymentRuleDTO>
)