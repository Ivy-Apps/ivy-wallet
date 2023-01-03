package com.ivy.wallet.ui

import com.ivy.common.isNotEmpty
import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.action.exchange.SyncExchangeRatesAct
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.domain.action.settings.theme.ThemeFlow
import com.ivy.data.CurrencyCode
import com.ivy.data.Theme
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
    baseCurrencyFlow: BaseCurrencyFlow,
    private val themeFlow: ThemeFlow,
) : FlowViewModel<RootViewModel.InternalState, RootState, RootEvent>() {
    override val initialState = InternalState(baseCurrency = "")

    override val stateFlow: Flow<InternalState> = baseCurrencyFlow().map { baseCurrency ->
        if (baseCurrency.isNotEmpty()) {
            Timber.i("Syncing exchange rates for $baseCurrency")
            syncExchangeRatesAct(baseCurrency)
        }
        InternalState(baseCurrency = baseCurrency)
    }

    override val initialUi = RootState(appLocked = false, theme = Theme.Auto)

    override val uiFlow: Flow<RootState> = themeFlow(Unit).map { theme ->
        RootState(
            appLocked = false,
            theme = theme
        )
    }


    // region Event Handling
    override suspend fun handleEvent(event: RootEvent) = when (event) {
        RootEvent.AppOpen -> handleAppOpen()
    }

    private suspend fun handleAppOpen() {
        if (!onboardingFinishedAct(Unit)) {
            delay(300) // TODO: Fix that
            // navigate to Onboarding
            navigator.navigate(Destination.onboarding.route) {
                popUpTo(Destination.main.route) {
                    inclusive = true
                }
            }
        }
    }
    // endregion

    data class InternalState(
        val baseCurrency: CurrencyCode,
    )
}