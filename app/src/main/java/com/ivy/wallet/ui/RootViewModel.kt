package com.ivy.wallet.ui

import com.ivy.common.isNotEmpty
import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.action.exchange.SyncExchangeRatesAct
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.data.CurrencyCode
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.Destination
import com.ivy.onboarding.action.OnboardingFinishedAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val onboardingFinishedAct: OnboardingFinishedAct,
    private val navigator: Navigator,
    private val syncExchangeRatesAct: SyncExchangeRatesAct,
    private val baseCurrencyFlow: BaseCurrencyFlow,
) : FlowViewModel<RootViewModel.State, RootState, RootEvent>() {
    override fun initialState() = State(baseCurrency = "")
    override fun initialUiState() = RootState(appLocked = false)

    override fun stateFlow(): Flow<State> = baseCurrencyFlow().map { baseCurrency ->
        if (baseCurrency.isNotEmpty()) {
            Timber.i("Syncing exchange rates for $baseCurrency")
            syncExchangeRatesAct(baseCurrency)
        }
        State(baseCurrency = baseCurrency)
    }

    override suspend fun mapToUiState(state: State): RootState = RootState(appLocked = false)

    // region Event Handling
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
    // endregion

    data class State(
        val baseCurrency: CurrencyCode,
    )
}