package com.ivy.wallet.io.network.request.planned

import com.ivy.wallet.domain.data.entity.PlannedPaymentRule

data class UpdatePlannedPaymentRuleRequest(
    val rule: PlannedPaymentRule? = null
)