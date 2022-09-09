package com.ivy.reports.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TrnType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.navigation
import com.ivy.reports.HeaderUiState
import com.ivy.reports.TAG
import com.ivy.reports.extensions.LogCompositions
import com.ivy.screens.PieChartStatistic
import com.ivy.wallet.ui.component.transaction.TransactionsDividerLine
import com.ivy.wallet.ui.theme.components.BalanceRow

@Composable
fun ReportsHeader(
    baseCurrency: CurrencyCode,
    headerUiState: HeaderUiState
) {
    LogCompositions(tag = TAG, msg = "Reports Header")

    ReportsTitle()

    Spacer(Modifier.height(8.dp))

    ReportsBalance(baseCurrency = baseCurrency, balance = headerUiState.balance)

    Spacer(Modifier.height(24.dp))

    IncomeExpensesCards(state = headerUiState, baseCurrency = baseCurrency)

    Spacer(Modifier.height(32.dp))

    TransactionsDividerLine(paddingHorizontal = 0.dp)

    Spacer(Modifier.height(4.dp))
}

@Composable
private fun ReportsTitle() {
    Text(
        modifier = Modifier.padding(
            start = 32.dp
        ),
        text = stringResource(R.string.reports),
        style = UI.typo.h2.style(
            fontWeight = FontWeight.ExtraBold
        )
    )
}

@Composable
private fun ReportsBalance(baseCurrency: CurrencyCode, balance: Double) {
    LogCompositions(tag = TAG, msg = "Reports Header + Balance")
    val amtBal by remember(balance) {
        mutableStateOf(
            when {
                balance > 0 -> "+"
                else -> null
            }
        )
    }
    BalanceRow(
        modifier = Modifier
            .padding(start = 32.dp),
        textColor = UI.colors.pureInverse,
        currency = baseCurrency,
        balance = balance,
        balanceAmountPrefix = amtBal
    )
}

@Composable
private fun IncomeExpensesCards(state: HeaderUiState, baseCurrency: CurrencyCode) {
    val nav = navigation()
    ReportsIncomeExpenseCards(
        currency = baseCurrency,
        income = state.income,
        expenses = state.expenses,
        incomeCount = state.incomeTransactionsCount,
        expenseCount = state.expenseTransactionsCount,
        hasAddButtons = false,
        itemColor = UI.colors.pure,
        incomeHeaderCardClicked = {
            if (state.transactionsOld.data.isNotEmpty())
                nav.navigateTo(
                    PieChartStatistic(
                        type = TrnType.INCOME,
                        transactions = state.transactionsOld.data,
                        accountList = state.accountIdFilters.data,
                        treatTransfersAsIncomeExpense = state.treatTransfersAsIncExp
                    )
                )
        },
        expenseHeaderCardClicked = {
            if (state.transactionsOld.data.isNotEmpty())
                nav.navigateTo(
                    PieChartStatistic(
                        type = TrnType.EXPENSE,
                        transactions = state.transactionsOld.data,
                        accountList = state.accountIdFilters.data,
                        treatTransfersAsIncomeExpense = state.treatTransfersAsIncExp
                    )
                )
        }
    )
}