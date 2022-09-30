package com.ivy.onboarding.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import javax.inject.Inject

class OnboardingKeys @Inject constructor() {
    val onboardingFinished by lazy { booleanPreferencesKey("onboarding_finished") }
}