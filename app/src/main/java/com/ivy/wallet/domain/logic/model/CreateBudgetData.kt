package com.ivy.wallet.domain.logic.model

data class CreateBudgetData(
    val name: String,
    val amount: Double,
    val categoryIdsSerialized: String,
    val accountIdsSerialized: String
)