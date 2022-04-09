package com.ivy.wallet.io.network.request.budget

import com.ivy.wallet.domain.data.entity.Budget


data class BudgetsResponse(
    val budgets: List<Budget>
)