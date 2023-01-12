package com.ivy.onboarding.action

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.onboarding.datastore.OnboardingKeys
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class OnboardingFinishedAct @Inject constructor(
    private val dataStore: IvyDataStore,
    private val onboardingKeys: OnboardingKeys,
) : Action<Unit, Boolean>() {
    override suspend fun action(input: Unit): Boolean =
        dataStore.get(onboardingKeys.onboardingFinished).first() ?: false
}