package com.ivy.wallet.network.request.budget

import com.ivy.wallet.model.entity.Budget


data class BudgetsResponse(
    val budgets: List<Budget>
)