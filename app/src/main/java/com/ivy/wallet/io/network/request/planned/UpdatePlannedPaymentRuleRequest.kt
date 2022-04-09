package com.ivy.wallet.io.network.request.planned

import com.ivy.wallet.model.entity.PlannedPaymentRule

data class UpdatePlannedPaymentRuleRequest(
    val rule: PlannedPaymentRule? = null
)