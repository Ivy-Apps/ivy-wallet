package com.ivy.onboarding.screen.debug

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.domain.action.settings.basecurrency.WriteBaseCurrencyAct
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.Destination
import com.ivy.onboarding.action.WriteOnboardingFinishedAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class OnboardingDebugViewModel @Inject constructor(
    private val writeBaseCurrencyAct: WriteBaseCurrencyAct,
    private val writeOnboardingFinishedAct: WriteOnboardingFinishedAct,
    baseCurrencyFlow: BaseCurrencyFlow,
    private val navigator: Navigator,
) : SimpleFlowViewModel<OnboardingDebugState, OnboardingDebugEvent>() {
    override val initialUi = OnboardingDebugState(baseCurrency = "")

    override val uiFlow: Flow<OnboardingDebugState> = baseCurrencyFlow().map {
        OnboardingDebugState(baseCurrency = it)
    }

    override suspend fun handleEvent(event: OnboardingDebugEvent) = when (event) {
        OnboardingDebugEvent.FinishOnboarding -> finishOnboarding()
        is OnboardingDebugEvent.SetBaseCurrency -> setBaseCurrency(event)
    }

    private suspend fun setBaseCurrency(event: OnboardingDebugEvent.SetBaseCurrency) {
        writeBaseCurrencyAct(event.currency)
    }

    private suspend fun finishOnboarding() {
        writeOnboardingFinishedAct(true)
        navigator.navigate(Destination.home.destination(Unit)) {
            popUpTo(Destination.debug.route) {
                inclusive = true
            }
        }
    }
}