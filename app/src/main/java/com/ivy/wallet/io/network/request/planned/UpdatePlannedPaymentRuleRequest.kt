package com.ivy.wallet.io.network.request.planned

import com.ivy.wallet.io.network.data.PlannedPaymentRuleDTO

data class UpdatePlannedPaymentRuleRequest(
    val rule: PlannedPaymentRuleDTO? = null
)