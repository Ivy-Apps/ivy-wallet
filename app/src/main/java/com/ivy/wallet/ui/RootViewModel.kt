package com.ivy.wallet.ui

import com.ivy.common.isNotBlank
import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.action.exchange.SyncExchangeRatesAct
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.Destination
import com.ivy.onboarding.action.OnboardingFinishedAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val onboardingFinishedAct: OnboardingFinishedAct,
    private val navigator: Navigator,
    private val syncExchangeRatesAct: SyncExchangeRatesAct,
    private val baseCurrencyFlow: BaseCurrencyFlow,
) : FlowViewModel<RootState, RootState, RootEvent>() {
    // TODO: Refactor this screen
    override fun initialState() = RootState(appLocked = false)

    override fun initialUiState() = initialState()

    override fun stateFlow(): Flow<RootState> =
        baseCurrencyFlow().map { baseCurrency ->
            if (baseCurrency.isNotBlank()) {
                syncExchangeRatesAct(baseCurrency)
            }
            RootState(appLocked = false)
        }

    override suspend fun mapToUiState(state: RootState) = state

    override suspend fun handleEvent(event: RootEvent) = when (event) {
        RootEvent.AppOpen -> handleAppOpen()
    }

    private suspend fun handleAppOpen() {
        if (!onboardingFinishedAct(Unit)) {
            delay(300) // TODO: Fix that
            // navigate to Onboarding
            navigator.navigate(Destination.onboarding.route) {
                popUpTo(Destination.onboarding.route) {
                    inclusive = true
                }
            }
        }
    }

}