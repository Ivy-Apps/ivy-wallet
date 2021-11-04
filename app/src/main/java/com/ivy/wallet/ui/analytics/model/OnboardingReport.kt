package com.ivy.wallet.ui.analytics.model

data class OnboardingReport(
    val onboardingStarted: Int?,
    val avgOnboardingStarted: Double?,

    val onboardingLogin: Int?,
    val onboardingPrivacyTCAccepted: Int?,
    val onboardingLocalAccount: Int?,
    val onboardingSetName: Int?,
    val onboardingSetCurrency: Int?,
    val onboardingAddAccount: Int?,
    val onboardingCompleted: Int?,
)