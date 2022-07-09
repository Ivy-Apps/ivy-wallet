package com.ivy.wallet.io.network.request.budget

import com.ivy.wallet.io.network.data.BudgetDTO


data class CrupdateBudgetRequest(
    val budget: BudgetDTO? = null
)