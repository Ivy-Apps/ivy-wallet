package com.ivy.wallet.ui.component.transaction

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
import com.ivy.base.R
import com.ivy.base.TransactionHistoryDateDivider
import com.ivy.base.data.AppBaseData
import com.ivy.base.data.DueSection
import com.ivy.data.transaction.TransactionOld
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.Navigation
import com.ivy.frp.view.navigation.navigation
import com.ivy.old.component.transaction.SectionDivider
import com.ivy.screens.EditTransaction
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.IvyIcon

fun LazyListScope.transactions(
    baseData: AppBaseData,

    upcoming: DueSection?,
    overdue: DueSection?,
    history: List<Any>,

    emptyStateTitle: String = com.ivy.core.ui.temp.stringRes(R.string.no_transactions),
    emptyStateText: String,

    dateDividerMarginTop: Dp? = null,
    lastItemSpacer: Dp? = null,

    onPayOrGet: (TransactionOld) -> Unit,
    setUpcomingExpanded: (Boolean) -> Unit,
    setOverdueExpanded: (Boolean) -> Unit,
    onSkipTransaction: (TransactionOld) -> Unit = {},
    onSkipAllTransactions: (List<TransactionOld>) -> Unit = {}
) {
    upcomingSection(
        baseData = baseData,
        upcoming = upcoming,

        onPayOrGet = onPayOrGet,
        onSkipTransaction = onSkipTransaction,
        setExpanded = setUpcomingExpanded
    )

    overdueSection(
        baseData = baseData,
        overdue = overdue,

        onPayOrGet = onPayOrGet,
        onSkipTransaction = onSkipTransaction,
        onSkipAllTransactions = onSkipAllTransactions,
        setExpanded = setOverdueExpanded
    )

    historySection(
        baseData = baseData,

        history = history,

        dateDividerMarginTop = dateDividerMarginTop,
        onPayOrGet = onPayOrGet
    )

    if (
        (upcoming == null || upcoming.trns.isEmpty()) &&
        (overdue == null || overdue.trns.isEmpty()) &&
        history.isEmpty()
    ) {
        item {
            NoTransactionsEmptyState(
                emptyStateTitle = emptyStateTitle,
                emptyStateText = emptyStateText
            )
        }
    }

    scrollHackSpacer(
        history = history,
        upcoming = upcoming,
        overdue = overdue,
        lastItemSpacer = lastItemSpacer
    )
}

private fun LazyListScope.upcomingSection(
    baseData: AppBaseData,

    upcoming: DueSection?,

    onPayOrGet: (TransactionOld) -> Unit,
    onSkipTransaction: (TransactionOld) -> Unit,
    setExpanded: (Boolean) -> Unit
) {
    if (upcoming == null) return //guard

    if (upcoming.trns.isNotEmpty()) {
        item(
            key = "upcoming_section"
        ) {
            SectionDivider(
                expanded = upcoming.expanded,
                setExpanded = setExpanded,
                title = com.ivy.core.ui.temp.stringRes(R.string.upcoming),
                titleColor = Orange,
                baseCurrency = baseData.baseCurrency,
                income = upcoming.stats.income.toDouble(),
                expenses = upcoming.stats.expense.abs().toDouble()
            )
        }

        if (upcoming.expanded) {
            trnItems(
                baseData = baseData,

                transactions = upcoming.trns,

                onPayOrGet = onPayOrGet,
                onSkipTransaction = onSkipTransaction
            )
        }
    }
}

private fun LazyListScope.overdueSection(
    baseData: AppBaseData,

    overdue: DueSection?,

    onPayOrGet: (TransactionOld) -> Unit,
    onSkipTransaction: (TransactionOld) -> Unit,
    onSkipAllTransactions: (List<TransactionOld>) -> Unit,
    setExpanded: (Boolean) -> Unit
) {
    if (overdue == null) return

    if (overdue.trns.isNotEmpty()) {
        item(
            key = "overdue_section"
        ) {
            SectionDivider(
                expanded = overdue.expanded,
                setExpanded = setExpanded,
                title = com.ivy.core.ui.temp.stringRes(R.string.overdue),
                titleColor = Red,
                baseCurrency = baseData.baseCurrency,
                income = overdue.stats.income.toDouble(),
                expenses = overdue.stats.expense.abs().toDouble()
            )
        }

        if (overdue.expanded) {
            item {
                val isLightTheme = UI.colors.pure == White
                IvyButton(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = com.ivy.core.ui.temp.stringRes(R.string.skip_all),
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
                    onSkipAllTransactions(overdue.trns)
                }
            }

            trnItems(
                baseData = baseData,

                transactions = overdue.trns,

                onPayOrGet = onPayOrGet,
                onSkipTransaction = onSkipTransaction
            )
        }
    }
}

private fun LazyListScope.trnItems(
    baseData: AppBaseData,

    transactions: List<TransactionOld>,

    onPayOrGet: (TransactionOld) -> Unit,
    onSkipTransaction: (TransactionOld) -> Unit,
) {
    items(
        items = transactions,
        key = { it.id }
    ) {
        val nav = navigation()
        TransactionCard(
            baseData = baseData,

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
    baseData: AppBaseData,

    history: List<Any>,

    dateDividerMarginTop: Dp? = null,

    onPayOrGet: (TransactionOld) -> Unit
) {
    if (history.isNotEmpty()) {
        items(
            items = history,
            key = {
                when (it) {
                    is TransactionOld -> it.id.toString()
                    is TransactionHistoryDateDivider -> it.date.toString()
                    else -> "unknown"
                }
            }
        ) {
            when (it) {
                is TransactionOld -> {
                    val nav = navigation()

                    TransactionCard(
                        baseData = baseData,

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
                        baseCurrency = baseData.baseCurrency,
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
    transaction: TransactionOld
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

private fun LazyListScope.scrollHackSpacer(
    history: List<Any>,
    upcoming: DueSection?,
    overdue: DueSection?,

    lastItemSpacer: Dp?,
) {
    item {
        if (lastItemSpacer != null) {
            Spacer(Modifier.height(lastItemSpacer))
        } else {
            //last spacer - scroll hack
            val trnCount = history.size.plus(
                if (upcoming != null && upcoming.expanded) upcoming.trns.size else 0
            ).plus(
                if (overdue != null && overdue.expanded) overdue.trns.size else 0
            )
            if (trnCount <= 5) {
                Spacer(Modifier.height(300.dp))
            } else {
                Spacer(Modifier.height(150.dp))
            }
        }
    }
}
