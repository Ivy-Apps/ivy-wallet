package com.ivy.onboarding.screen.debug

import androidx.compose.runtime.Immutable
import com.ivy.data.CurrencyCode

@Immutable
data class OnboardingDebugState(
    val baseCurrency: CurrencyCode
)