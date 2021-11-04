package com.ivy.wallet.ui.analytics.model

data class KPIs(
    val onboardedUsers: Int,

    val usersCreatedAccount: Int,
    val avgAccountsPerUser: Double,

    val usersCreatedCategory: Int,
    val avgCategoriesPerUser: Double,

    val usersCreatedTransaction: Int,
    val avgTransactionsPerUser: Double,

    val usersCreatedPlannedPayment: Int,
    val avgPlannedPaymentsPerUser: Double,

    //Paywall
    val paywallAccounts: Int,
    val avgPaywallAccounts: Double?,

    val paywallCategories: Int,
    val avgPaywallCategories: Double?,

    val paywallNoReason: Int,
    val avgPaywallNoReason: Double?,

    val paywallExportCSV: Int,
    val avgPaywallExportCSV: Double?,

    val paywallPremiumColor: Int,
    val avgPaywallPremiumColor: Double?,

    //Premium
    val choosePlan: Int,
    val choosePlanMonthly: Int,
    val choosePlan6Month: Int,
    val choosePlanYearly: Int,
    val choosePlanLifetime: Int,

    val startBuyPremium: Int,
    val startBuyPremiumMonthly: Int,
    val startBuyPremium6Month: Int,
    val startBuyPremiumYearly: Int,
    val startBuyPremiumLifetime: Int,

    val activePremium: Int
)