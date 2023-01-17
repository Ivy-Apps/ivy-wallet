package com.ivy.core.ui.account.adjustbalance

import com.ivy.core.domain.FlowViewModel
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.domain.action.account.AdjustAccBalanceAct
import com.ivy.core.domain.action.exchange.ExchangeRatesFlow
import com.ivy.core.domain.pure.exchange.exchange
import com.ivy.core.ui.account.adjustbalance.AdjustBalanceViewModel.State
import com.ivy.core.ui.account.adjustbalance.data.AdjustType
import com.ivy.data.account.Account
import com.ivy.data.exchange.ExchangeRates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class AdjustBalanceViewModel @Inject constructor(
    exchangeRatesFlow: ExchangeRatesFlow,
    private val adjustAccBalanceAct: AdjustAccBalanceAct,
    private val accountsFlow: AccountsFlow,
) : FlowViewModel<State, AdjustBalanceState, AdjustBalanceEvent>() {
    override val initialState: State = State(
        ratesData = ExchangeRates(
            baseCurrency = "",
            rates = emptyMap(),
        ),
        account = null,
    )

    override val initialUi = AdjustBalanceState(
        adjustType = AdjustType.WithTransaction,
    )

    private val accountId = MutableStateFlow("")
    private val adjustType = MutableStateFlow(AdjustType.WithTransaction)

    override val stateFlow: Flow<State> = combine(
        exchangeRatesFlow(), accountFlow(),
    ) { ratesData, account ->
        State(
            ratesData = ratesData,
            account = account,
        )
    }

    private fun accountFlow(): Flow<Account?> = combine(
        accountsFlow(), accountId
    ) { accounts, accountId ->
        accounts.firstOrNull { it.id.toString() == accountId }
    }

    override val uiFlow: Flow<AdjustBalanceState> = adjustType.map { adjustType ->
        AdjustBalanceState(
            adjustType = adjustType,
        )
    }

    // region Event handling
    override suspend fun handleEvent(event: AdjustBalanceEvent) = when (event) {
        is AdjustBalanceEvent.Initial -> handleInitial(event)
        is AdjustBalanceEvent.AdjustBalance -> handleAdjustBalance(event)
        is AdjustBalanceEvent.AdjustTypeChange -> handleAdjustTypeChange(event)
    }

    private fun handleInitial(event: AdjustBalanceEvent.Initial) {
        accountId.value = event.accountId
    }

    private suspend fun handleAdjustBalance(event: AdjustBalanceEvent.AdjustBalance) {
        val account = state.value.account ?: return
        val accountAmount = exchange(
            exchangeData = state.value.ratesData,
            from = event.balance.currency,
            to = account.currency,
            amount = event.balance.amount
        ).orNull() ?: return

        adjustAccBalanceAct(
            AdjustAccBalanceAct.Input(
                account = account,
                desiredBalance = accountAmount,
                hideTransaction = when (adjustType.value) {
                    AdjustType.WithTransaction -> false
                    AdjustType.NoTransaction -> true
                }
            )
        )
    }

    private fun handleAdjustTypeChange(event: AdjustBalanceEvent.AdjustTypeChange) {
        adjustType.value = event.type
    }
    // endregion

    data class State(
        val ratesData: ExchangeRates,
        val account: Account?,
    )
}