package com.ivy.wallet.ui.theme.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.wallet.R
import com.ivy.wallet.model.TransactionHistoryDateDivider
import com.ivy.wallet.model.TransactionHistoryItem
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IvyIcon
import kotlin.math.absoluteValue

fun LazyListScope.transactions(
    ivyContext: IvyContext,
    upcoming: List<Transaction>,
    upcomingExpanded: Boolean,
    setUpcomingExpanded: (Boolean) -> Unit,
    baseCurrency: String,
    upcomingIncome: Double,
    upcomingExpenses: Double,
    categories: List<Category>,
    accounts: List<Account>,
    listState: LazyListState,
    overdue: List<Transaction>,
    overdueExpanded: Boolean,
    setOverdueExpanded: (Boolean) -> Unit,
    overdueIncome: Double,
    overdueExpenses: Double,
    history: List<TransactionHistoryItem>,
    lastItemSpacer: Dp? = null,
    onPayOrGet: (Transaction) -> Unit,
    emptyStateTitle: String = "No transactions",

    emptyStateText: String
) {
    if (upcoming.isNotEmpty()) {
        item {
            SectionDivider(
                expanded = upcomingExpanded,
                setExpanded = setUpcomingExpanded,
                title = "Upcoming",
                titleColor = Orange,
                baseCurrency = baseCurrency,
                income = upcomingIncome,
                expenses = upcomingExpenses.absoluteValue
            )
        }

        if (upcomingExpanded) {
            items(upcoming) {
                TransactionCard(
                    baseCurrency = baseCurrency,
                    categories = categories,
                    accounts = accounts,
                    transaction = it,
                    onPayOrGet = onPayOrGet
                ) { trn ->
                    onTransactionClick(
                        ivyContext = ivyContext,
                        listState = listState,
                        transaction = trn
                    )
                }
            }
        }
    }

    if (overdue.isNotEmpty()) {
        item {
            SectionDivider(
                expanded = overdueExpanded,
                setExpanded = setOverdueExpanded,
                title = "Overdue",
                titleColor = Red,
                baseCurrency = baseCurrency,
                income = overdueIncome,
                expenses = overdueExpenses.absoluteValue
            )
        }

        if (overdueExpanded) {
            items(overdue) {
                TransactionCard(
                    baseCurrency = baseCurrency,
                    categories = categories,
                    accounts = accounts,
                    transaction = it,
                    onPayOrGet = onPayOrGet
                ) { trn ->
                    onTransactionClick(
                        ivyContext = ivyContext,
                        listState = listState,
                        transaction = trn
                    )
                }
            }
        }
    }

    if (history.isNotEmpty()) {
        items(history) {
            when (it) {
                is Transaction -> {
                    TransactionCard(
                        baseCurrency = baseCurrency,
                        categories = categories,
                        accounts = accounts,
                        transaction = it,
                        onPayOrGet = onPayOrGet
                    ) { trn ->
                        onTransactionClick(
                            ivyContext = ivyContext,
                            listState = listState,
                            transaction = trn
                        )
                    }
                }

                is TransactionHistoryDateDivider -> {
                    HistoryDateDivider(
                        date = it.date,
                        spacerTop = if (it == history.firstOrNull()) 24.dp else 32.dp,
                        baseCurrency = baseCurrency,
                        income = it.income,
                        expenses = it.expenses
                    )
                }
            }
        }
    }

    if (upcoming.isEmpty() && overdue.isEmpty() && history.isEmpty()) {
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
            val trnCount = history.size + if (upcomingExpanded) upcoming.size else 0 +
                    if (overdueExpanded) overdue.size else 0
            if (trnCount <= 5) {
                Spacer(Modifier.height(300.dp))
            } else {
                Spacer(Modifier.height(150.dp))
            }
        }
    }
}

private fun onTransactionClick(
    ivyContext: IvyContext,
    listState: LazyListState,
    transaction: Transaction
) {
    ivyContext.navigateTo(
        Screen.EditTransaction(
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
            style = Typo.body1.style(
                color = Gray,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = emptyStateText,
            style = Typo.body2.style(
                color = Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(96.dp))
    }
}
