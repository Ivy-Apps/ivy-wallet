package com.ivy.wallet.ui.analytics.model

data class UserStats(
    val powerUsers: Int,
    val avgPowerUsersTrns: Double,

    val activeUsers: Int,
    val avgActiveUsersTrns: Double,

    val dyingUsers: Int,
    val avgDyingUsers: Double,
)