package com.ivy.onboarding.action

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.onboarding.datastore.OnboardingKeys
import javax.inject.Inject

class WriteOnboardingFinishedAct @Inject constructor(
    private val dataStore: IvyDataStore,
    private val onboardingKeys: OnboardingKeys,
) : Action<Boolean, Unit>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(finished: Boolean) {
        dataStore.put(onboardingKeys.onboardingFinished, finished)
    }
}