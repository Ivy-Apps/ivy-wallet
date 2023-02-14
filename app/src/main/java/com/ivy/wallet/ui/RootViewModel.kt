package com.ivy.wallet.ui

import android.content.Intent
import com.ivy.common.isNotEmpty
import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.action.exchange.SyncExchangeRatesAct
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.domain.action.settings.theme.ThemeFlow
import com.ivy.data.CurrencyCode
import com.ivy.data.Theme
import com.ivy.data.transaction.TransactionType
import com.ivy.drive.google_drive.api.GoogleDriveConnection
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.Destination
import com.ivy.navigation.destinations.transaction.NewTransaction
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
    private val googleDriveConnection: GoogleDriveConnection
) : FlowViewModel<RootViewModel.InternalState, RootState, RootEvent>() {
    companion object {
        const val EXTRA_ADD_TRANSACTION_TYPE = "add_transaction_type_extra"
    }

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
    override suspend fun handleEvent(event: RootEvent) {
        when (event) {
            is RootEvent.AppOpen -> {
                handleAppOpen()
            }

            is RootEvent.ShortcutClick -> {
                handleShortcut(event.intent)
            }
        }
    }

    private suspend fun handleAppOpen() {
        if (!onboardingFinishedAct(Unit)) {
            delay(300) // TODO: Fix that
            // navigate to Onboarding
            navigator.navigate(Destination.onboarding.route) {
                popUpTo(Destination.home.route) {
                    inclusive = true
                }
            }
        }

        googleDriveConnection.mount()
    }

    //function to handle shortcut action clicks
    private fun handleShortcut(intent: Intent) {
        when (intent.getStringExtra(EXTRA_ADD_TRANSACTION_TYPE)) {
            // Add expense shortcut
            "EXPENSE" -> {
                navigator.navigate(
                    Destination.newTransaction.destination(
                        NewTransaction.Arg(trnType = TransactionType.Expense)
                    )
                )
            }
            // Add income shortcut
            "INCOME" -> {
                navigator.navigate(
                    Destination.newTransaction.destination(
                        NewTransaction.Arg(trnType = TransactionType.Income)
                    )
                )
            }
            // Add transfer shortcut
            "TRANSFER" -> {
                navigator.navigate(Destination.newTransfer.destination(Unit))
            }
        }
    }

    // endregion

    data class InternalState(
        val baseCurrency: CurrencyCode,
    )
}