package com.ivy.onboarding.screen.debug

import com.ivy.data.CurrencyCode

sealed interface OnboardingDebugEvent {
    data class SetBaseCurrency(val currency: CurrencyCode) : OnboardingDebugEvent
    object FinishOnboarding : OnboardingDebugEvent
}