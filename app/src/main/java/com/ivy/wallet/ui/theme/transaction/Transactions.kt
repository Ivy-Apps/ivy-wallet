package com.ivy.wallet.ui.theme.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.Navigation
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.TransactionHistoryDateDivider
import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import com.ivy.wallet.stringRes
import com.ivy.wallet.ui.EditTransaction
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.IvyIcon

fun LazyListScope.transactions(
    baseCurrency: String,
    accounts: List<Account>,
    categories: List<Category>,

    upcomingTrns: List<Transaction>,
    upcomingExpanded: Boolean,
    upcoming: IncomeExpensePair,

    overdueTrns: List<Transaction>,
    overdueExpanded: Boolean,
    overdue: IncomeExpensePair,

    history: List<TransactionHistoryItem>,

    emptyStateTitle: String = stringRes(R.string.no_transactions),
    emptyStateText: String,

    dateDividerMarginTop: Dp? = null,
    lastItemSpacer: Dp? = null,

    onPayOrGet: (Transaction) -> Unit,
    setUpcomingExpanded: (Boolean) -> Unit,
    setOverdueExpanded: (Boolean) -> Unit,
    onSkipTransaction: (Transaction) -> Unit = {},
    onSkipAllTransactions: (List<Transaction>) -> Unit = {}
) {
    upcomingSection(
        baseCurrency = baseCurrency,
        accounts = accounts,
        categories = categories,
        upcomingTrns = upcomingTrns,
        upcomingExpanded = upcomingExpanded,
        upcoming = upcoming,
        onPayOrGet = onPayOrGet,
        setUpcomingExpanded = setUpcomingExpanded,
        onSkipTransaction = onSkipTransaction
    )

    overdueSection(
        baseCurrency = baseCurrency,
        accounts = accounts,
        categories = categories,
        overdueTrns = overdueTrns,
        overdueExpanded = overdueExpanded,
        overdue = overdue,
        onPayOrGet = onPayOrGet,
        setOverdueExpanded = setOverdueExpanded,
        onSkipTransaction = onSkipTransaction,
        onSkipAllTransactions = onSkipAllTransactions
    )

    historySection(
        baseCurrency = baseCurrency,
        accounts = accounts,
        categories = categories,

        history = history,

        dateDividerMarginTop = dateDividerMarginTop,

        onPayOrGet = onPayOrGet
    )

    if (upcomingTrns.isEmpty() && overdueTrns.isEmpty() && history.isEmpty()) {
        item {
            NoTransactionsEmptyState(
                emptyStateTitle = emptyStateTitle,
                emptyStateText = emptyStateText
            )
        }
    }

    item {
        if (lastItemSpacer != null) {
            Spacer(Modifier.height(lastItemSpacer))
        } else {
            //last spacer - scroll hack
            val trnCount = history.size + if (upcomingExpanded) upcomingTrns.size else 0 +
                    if (overdueExpanded) overdueTrns.size else 0
            if (trnCount <= 5) {
                Spacer(Modifier.height(300.dp))
            } else {
                Spacer(Modifier.height(150.dp))
            }
        }
    }
}

private fun LazyListScope.upcomingSection(
    baseCurrency: String,
    accounts: List<Account>,
    categories: List<Category>,

    upcomingTrns: List<Transaction>,
    upcomingExpanded: Boolean,
    upcoming: IncomeExpensePair,

    onPayOrGet: (Transaction) -> Unit,
    setUpcomingExpanded: (Boolean) -> Unit,
    onSkipTransaction: (Transaction) -> Unit,
) {
    if (upcomingTrns.isNotEmpty()) {
        item {
            SectionDivider(
                expanded = upcomingExpanded,
                setExpanded = setUpcomingExpanded,
                title = stringRes(R.string.upcoming),
                titleColor = Orange,
                baseCurrency = baseCurrency,
                income = upcoming.income.toDouble(),
                expenses = upcoming.expense.abs().toDouble()
            )
        }

        if (upcomingExpanded) {
            trnItems(
                baseCurrency = baseCurrency,
                accounts = accounts,
                categories = categories,

                transactions = upcomingTrns,

                onPayOrGet = onPayOrGet,
                onSkipTransaction = onSkipTransaction
            )
        }
    }
}

private fun LazyListScope.overdueSection(
    baseCurrency: String,
    accounts: List<Account>,
    categories: List<Category>,

    overdueTrns: List<Transaction>,
    overdueExpanded: Boolean,
    overdue: IncomeExpensePair,

    onPayOrGet: (Transaction) -> Unit,
    setOverdueExpanded: (Boolean) -> Unit,
    onSkipTransaction: (Transaction) -> Unit,
    onSkipAllTransactions: (List<Transaction>) -> Unit
) {
    if (overdueTrns.isNotEmpty()) {
        item {
            SectionDivider(
                expanded = overdueExpanded,
                setExpanded = setOverdueExpanded,
                title = stringRes(R.string.overdue),
                titleColor = Red,
                baseCurrency = baseCurrency,
                income = overdue.income.toDouble(),
                expenses = overdue.expense.abs().toDouble()
            )
        }

        if (overdueExpanded) {
            item {
                val isLightTheme = UI.colors.pure == White
                IvyButton(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringRes(R.string.skip_all),
                    wrapContentMode = false,
                    backgroundGradient = if (isLightTheme) Gradient(White, White) else Gradient(
                        Black,
                        Black
                    ),
                    textStyle = UI.typo.b2.style(
                        color = if (isLightTheme) Black else White,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    onSkipAllTransactions(overdueTrns)
                }
            }

            trnItems(
                baseCurrency = baseCurrency,
                accounts = accounts,
                categories = categories,

                transactions = overdueTrns,

                onPayOrGet = onPayOrGet,
                onSkipTransaction = onSkipTransaction
            )
        }
    }
}

private fun LazyListScope.trnItems(
    baseCurrency: String,
    accounts: List<Account>,
    categories: List<Category>,

    transactions: List<Transaction>,

    onPayOrGet: (Transaction) -> Unit,
    onSkipTransaction: (Transaction) -> Unit,
) {
    items(
        items = transactions,
        key = { it.id }
    ) {
        val nav = navigation()
        TransactionCard(
            baseCurrency = baseCurrency,
            categories = categories,
            accounts = accounts,
            transaction = it,
            onPayOrGet = onPayOrGet,
            onSkipTransaction = onSkipTransaction
        ) { trn ->
            onTransactionClick(
                nav = nav,
                transaction = trn
            )
        }
    }
}

private fun LazyListScope.historySection(
    baseCurrency: String,
    accounts: List<Account>,
    categories: List<Category>,

    history: List<TransactionHistoryItem>,

    dateDividerMarginTop: Dp? = null,

    onPayOrGet: (Transaction) -> Unit
) {
    if (history.isNotEmpty()) {
        items(
            items = history,
            key = {
                when (it) {
                    is Transaction -> it.id.toString()
                    is TransactionHistoryDateDivider -> it.date.toString()
                    else -> "unknown"
                }
            }
        ) {
            when (it) {
                is Transaction -> {
                    val nav = navigation()

                    TransactionCard(
                        baseCurrency = baseCurrency,
                        categories = categories,
                        accounts = accounts,
                        transaction = it,
                        onPayOrGet = onPayOrGet
                    ) { trn ->
                        onTransactionClick(
                            nav = nav,
                            transaction = trn
                        )
                    }
                }

                is TransactionHistoryDateDivider -> {
                    HistoryDateDivider(
                        date = it.date,
                        spacerTop = dateDividerMarginTop
                            ?: if (it == history.firstOrNull()) 24.dp else 32.dp,
                        baseCurrency = baseCurrency,
                        income = it.income,
                        expenses = it.expenses
                    )
                }
            }
        }
    }
}

private fun onTransactionClick(
    nav: Navigation,
    transaction: Transaction
) {
    nav.navigateTo(
        EditTransaction(
            initialTransactionId = transaction.id,
            type = transaction.type
        )
    )
}

@Composable
private fun LazyItemScope.NoTransactionsEmptyState(
    emptyStateTitle: String,
    emptyStateText: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        IvyIcon(
            icon = R.drawable.ic_notransactions,
            tint = Gray
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = emptyStateTitle,
            style = UI.typo.b1.style(
                color = Gray,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = emptyStateText,
            style = UI.typo.b2.style(
                color = Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(96.dp))
    }
}
