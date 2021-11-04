package com.ivy.wallet.ui.analytics.model

data class Health(
    val usersWithTransactionLast24h: Int,
    val avgTransactionsLast24: Double,

    val usersWithTransactionLast7Days: Int,
    val avgTransactionsLast7Days: Double,

    val usersWithTransactionLast30Days: Int,
    val avgTransactionsLast30Days: Double,

    val loggedFromSettings: Int,
)