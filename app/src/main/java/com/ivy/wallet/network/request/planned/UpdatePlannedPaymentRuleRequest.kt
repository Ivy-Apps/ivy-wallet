package com.ivy.wallet.network.request.planned

import com.ivy.wallet.model.entity.PlannedPaymentRule

data class UpdatePlannedPaymentRuleRequest(
    val rule: PlannedPaymentRule? = null
)