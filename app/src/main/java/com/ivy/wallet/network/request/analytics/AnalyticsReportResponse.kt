package com.ivy.wallet.network.request.analytics

data class AnalyticsReportResponse(
    //Events -----------------------------------------------------
    val eventsCount: Map<String, Int>,
    val eventAvgCount: Map<String, Double>,

    //KPIs
    val onboardedUsersCount: Int,
    val usersCreatedAccount: List<Int>,
    val usersCreatedCategory: List<Int>,
    val usersCreatedTransaction: List<Int>,
    val usersCreatedPlannedPayment: List<Int>,

    //Health
    val usersWithTransactionLast24H: List<Int>,
    val usersWithTransactionLast7Days: List<Int>,
    val usersWithTransactionLast30Days: List<Int>,

    //User Stats
    val powerUsersV1: List<Int>, //>= 14 per week (2 transactions per day = 14 per week)
    val activeUsersV1: List<Int>, //5-13 transactions per week
    val dyingUsersV1: List<Int>, //1-4 transaction per week
)