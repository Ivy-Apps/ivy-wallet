package com.ivy.home

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.algorithm.balance.TotalBalanceFlow
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.core.ui.algorithm.trnhistory.PeriodDataFlow
import com.ivy.core.ui.algorithm.trnhistory.data.PeriodDataUi
import com.ivy.data.Value
import com.ivy.data.transaction.TransactionType
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.Destination
import com.ivy.navigation.destinations.transaction.NewTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val balanceFlow: TotalBalanceFlow,
    private val periodDataFlow: PeriodDataFlow,
    private val navigator: Navigator,
) : SimpleFlowViewModel<HomeStateUi, HomeEvent>() {
    private val addTransactionModal = IvyModal()

    override val initialUi = HomeStateUi(
        balance = ValueUi(amount = "0.0", currency = ""),
        income = ValueUi(amount = "0.0", currency = ""),
        expense = ValueUi(amount = "0.0", currency = ""),
        trnListItems = emptyList(),

        hideBalance = false,
        bottomBarVisible = true,

        addTransactionModal = addTransactionModal,
    )

    private val overrideShowBalance = MutableStateFlow(false)
    private val bottomBarVisible = MutableStateFlow(initialUi.bottomBarVisible)

    // region UI flow
    override val uiFlow: Flow<HomeStateUi> = combine(
        immediateBalanceFlow(), immediatePeriodDataFlow(), bottomBarVisible
    ) { balance, trnsList, bottomBarVisible ->
        HomeStateUi(
            balance = formatBalance(balance),
            income = trnsList.periodIncome,
            expense = trnsList.periodExpense,
            trnListItems = trnsList.items,

            hideBalance = false, // TODO: Implement hide balance
            bottomBarVisible = bottomBarVisible,

            addTransactionModal = addTransactionModal,
        )
    }

    private fun immediateBalanceFlow() = balanceFlow(
        TotalBalanceFlow.Input(withExcluded = false)
    ).onStart {
        emit(Value(amount = 0.0, currency = ""))
    }

    private fun immediatePeriodDataFlow() = periodDataFlow(
        PeriodDataFlow.Input.All
    ).onStart {
        emit(
            PeriodDataUi(
                periodIncome = ValueUi("", ""),
                periodExpense = ValueUi("", ""),
                items = emptyList()
            )
        )
    }

    private fun formatBalance(balance: Value): ValueUi = format(
        value = balance,
        shortenFiat = balance.amount > 10_000
    )
    // endregion

    // region Event Handling
    override suspend fun handleEvent(event: HomeEvent) = when (event) {
        HomeEvent.AddExpense -> handleAddExpense()
        HomeEvent.AddIncome -> handleAddIncome()
        HomeEvent.AddTransfer -> handleAddTransfer()
        HomeEvent.BalanceClick -> handleBalanceClick()
        HomeEvent.HiddenBalanceClick -> handleHiddenBalanceClick()
        HomeEvent.ExpenseClick -> handleExpenseClick()
        HomeEvent.IncomeClick -> handleIncomeClick()
        HomeEvent.MoreClick -> handleMoreClick()
        is HomeEvent.BottomBar -> handleBottomBarEvents(event)
    }

    private fun handleBottomBarEvents(event: HomeEvent.BottomBar) {
        when (event) {
            HomeEvent.BottomBar.AccountsClick -> {
                navigator.navigate(Destination.accounts.destination(Unit))
            }
            HomeEvent.BottomBar.AddClick -> {
                addTransactionModal.show()
            }
            HomeEvent.BottomBar.Hide -> {
                bottomBarVisible.value = false
            }
            HomeEvent.BottomBar.Show -> {
                bottomBarVisible.value = true
            }
        }
    }

    private fun handleAddTransfer() {
        navigator.navigate(Destination.newTransfer.destination(Unit))
    }

    private fun handleAddIncome() {
        navigator.navigate(
            Destination.newTransaction.destination(
                NewTransaction.Arg(trnType = TransactionType.Income)
            )
        )
    }

    private fun handleAddExpense() {
        navigator.navigate(
            Destination.newTransaction.destination(
                NewTransaction.Arg(trnType = TransactionType.Expense)
            )
        )
    }

    private fun handleBalanceClick() {
        // TODO: Implement
    }

    private fun handleExpenseClick() {
        // TODO: Implement
    }

    private fun handleIncomeClick() {
        // TODO: Implement
    }

    private suspend fun handleHiddenBalanceClick() {
        overrideShowBalance.value = true
        delay(3_000L)
        overrideShowBalance.value = false
    }

    private fun handleMoreClick() {
        navigator.navigate(Destination.moreMenu.destination(Unit))
    }
    // endregion
}