package com.ivy.core.ui.transaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.common.time.timeNow
import com.ivy.core.domain.algorithm.trnhistory.OverdueSectionKey
import com.ivy.core.domain.algorithm.trnhistory.UpcomingSectionKey
import com.ivy.core.domain.algorithm.trnhistory.toggleCollapseExpandTrnListKey
import com.ivy.core.domain.pure.format.SignedValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.algorithm.trnhistory.data.*
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.icon.dummyIconSized
import com.ivy.core.ui.data.icon.dummyIconUnknown
import com.ivy.core.ui.data.transaction.dummyTrnTimeActualUi
import com.ivy.core.ui.data.transaction.dummyTrnTimeDueUi
import com.ivy.core.ui.transaction.handling.DueActionsHandler
import com.ivy.core.ui.transaction.handling.TrnItemClickHandler
import com.ivy.core.ui.transaction.handling.defaultDueActionsHandler
import com.ivy.core.ui.transaction.handling.defaultTrnItemClickHandler
import com.ivy.core.ui.transaction.item.DateDivider
import com.ivy.core.ui.transaction.item.DueSectionDivider
import com.ivy.core.ui.transaction.item.TransactionCard
import com.ivy.core.ui.transaction.item.TransferCard
import com.ivy.data.transaction.TransactionType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.*
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.IconRes
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.util.IvyPreview
import com.ivy.resources.R

// region EmptyState data
@Immutable
data class EmptyState(
    val title: String,
    val description: String,
)

@Composable
fun defaultEmptyState() = EmptyState(
    title = stringResource(R.string.no_transactions),
    description = stringResource(R.string.no_transactions_desc)
)
// endregion

internal fun LazyListScope.transactionsList(
    items: List<TrnListItemUi>,
    emptyState: EmptyState,
    trnClickHandler: TrnItemClickHandler,
    dueActionsHandler: DueActionsHandler,
) {
    trnListItems(
        items = items,
        trnClickHandler = trnClickHandler,
        dueActionsHandler = dueActionsHandler,
    )

    val isEmpty by derivedStateOf { items.isEmpty() }
    if (isEmpty) {
        emptyState(emptyState)
    }
}

private fun LazyListScope.trnListItems(
    items: List<TrnListItemUi>,
    trnClickHandler: TrnItemClickHandler,
    dueActionsHandler: DueActionsHandler,
) {
    items(
        items = items,
        key = { item ->
            when (item) {
                is DateDividerUi -> item.id
                is DueDividerUi -> item.id
                is TransactionUi -> item.id
                is TransferUi -> item.batchId
            }
        }
    ) { item ->
        when (item) {
            is DateDividerUi -> {
                SpacerVer(height = 16.dp)
                DateDivider(item)
            }
            is DueDividerUi -> {
                SpacerVer(height = 16.dp)
                DueSectionDivider(
                    divider = item,
                    setExpanded = {
                        toggleCollapseExpandTrnListKey(
                            when (item.type) {
                                DueDividerUiType.Upcoming -> UpcomingSectionKey
                                DueDividerUiType.Overdue -> OverdueSectionKey
                            }
                        )
                    }
                )
            }
            is TransactionUi -> {
                SpacerVer(height = 12.dp)
                TransactionCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    trn = item,
                    onClick = trnClickHandler.onTrnClick,
                    onAccountClick = trnClickHandler.onAccountClick,
                    onCategoryClick = trnClickHandler.onCategoryClick,
                    onExecute = dueActionsHandler.onExecuteTrn,
                    onSkip = dueActionsHandler.onSkipTrn
                )
            }
            is TransferUi -> {
                SpacerVer(height = 12.dp)
                TransferCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    transfer = item,
                    onClick = trnClickHandler.onTransferClick,
                    onAccountClick = trnClickHandler.onAccountClick,
                    onCategoryClick = trnClickHandler.onCategoryClick,
                    onExecuteTransfer = dueActionsHandler.onExecuteTransfer,
                    onSkipTransfer = dueActionsHandler.onSkipTransfer,
                )
            }
        }
    }
}

private fun LazyListScope.emptyState(emptyState: EmptyState) {
    item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconRes(icon = R.drawable.ic_notransactions, tint = UI.colors.neutral)
            SpacerVer(height = 24.dp)
            B1(
                text = emptyState.title,
                modifier = Modifier.fillMaxWidth(),
                color = UI.colors.neutral,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )
            SpacerVer(height = 8.dp)
            B2(
                text = emptyState.description,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                color = UI.colors.neutral,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

// region Previews
@Preview
@Composable
private fun Preview_Full() {
    IvyPreview {
        val emptyState = defaultEmptyState()
        val items = sampleTrnListItems()
        val trnItemClickHandler = defaultTrnItemClickHandler()
        val dueActionsHandler = defaultDueActionsHandler()


        LazyColumn {
            transactionsList(
                items = items,
                emptyState = emptyState,
                trnClickHandler = trnItemClickHandler,
                dueActionsHandler = dueActionsHandler,
            )
        }
    }
}

@Preview
@Composable
private fun Preview_EmptyState() {
    IvyPreview {
        val emptyState = defaultEmptyState()
        val trnItemClickHandler = defaultTrnItemClickHandler()
        val dueActionsHandler = defaultDueActionsHandler()

        LazyColumn {
            transactionsList(
                items = emptyList(),
                emptyState = emptyState,
                trnClickHandler = trnItemClickHandler,
                dueActionsHandler = dueActionsHandler,
            )
        }
    }
}

@Composable
fun sampleTrnListItems(): List<TrnListItemUi> = listOf(
    dummyDueDividerUi(
        label = "Upcoming",
        type = DueDividerUiType.Upcoming,
        income = dummyValueUi("16.99"),
        expense = null
    ),
    dummyTransactionUi(
        title = "Upcoming payment",
        account = dummyAccountUi(
            name = "Revolut",
            color = Purple,
            icon = dummyIconSized(R.drawable.ic_custom_revolut_s)
        ),
        category = dummyCategoryUi(
            name = "Investments",
            color = Blue2Light,
            icon = dummyIconSized(R.drawable.ic_custom_leaf_s)
        ),
        value = dummyValueUi("16.99"),
        type = TransactionType.Income,
        time = dummyTrnTimeDueUi(timeNow().plusDays(1))
    ),
    dummyDueDividerUi(
        type = DueDividerUiType.Overdue,
        label = "Overdue",
        income = null,
        expense = dummyValueUi("650.0"),
    ),
    dummyTransactionUi(
        title = "Rent",
        value = dummyValueUi("650.0"),
        account = dummyAccountUi(
            name = "Cash",
            color = Green,
            icon = dummyIconUnknown(R.drawable.ic_vue_money_coins)
        ),
        category = null,
        type = TransactionType.Expense,
        time = dummyTrnTimeDueUi()
    ),
    DateDividerUi(
        id = "2021-01-01",
        date = "September 25.",
        dateContext = "Friday",
        cashflow = SignedValueUi.Negative(dummyValueUi("30.0")),
        collapsed = false,
    ),
    dummyTransactionUi(
        title = "Food",
        account = dummyAccountUi(
            name = "Revolut",
            color = Purple,
            icon = dummyIconSized(R.drawable.ic_custom_revolut_s)
        ),
        category = dummyCategoryUi(
            name = "Order food",
            color = Orange2,
            icon = dummyIconSized(R.drawable.ic_custom_orderfood_s)
        ),
        value = dummyValueUi("30.0"),
        type = TransactionType.Expense,
        time = dummyTrnTimeActualUi()
    ),
    DateDividerUi(
        id = "2021-01-01",
        date = "September 23.",
        dateContext = "Wednesday",
        cashflow = SignedValueUi.Positive(dummyValueUi("105.33")),
        collapsed = false,
    ),
    dummyTransactionUi(
        title = "Buy some cool gadgets",
        description = "Premium tech!",
        account = dummyAccountUi(
            name = "Bank",
            color = Red,
            icon = dummyIconSized(R.drawable.ic_custom_bank_s)
        ),
        category = dummyCategoryUi(
            name = "Tech",
            color = Blue2Dark,
            icon = dummyIconUnknown(R.drawable.ic_vue_edu_telescope)
        ),
        value = dummyValueUi("55.23"),
        type = TransactionType.Expense,
    ),
    dummyTransactionUi(
        title = "Ivy Apps revenue",
        account = dummyAccountUi(
            name = "Revolut Business",
            color = Purple2Dark,
            icon = dummyIconSized(R.drawable.ic_custom_revolut_s)
        ),
        category = null,
        value = dummyValueUi("160.53"),
        type = TransactionType.Income,
    ),
    dummyTransactionUi(
        title = "Buy some cool gadgets",
        description = "Premium tech!",
        account = dummyAccountUi(
            name = "Bank",
            color = Red,
            icon = dummyIconSized(R.drawable.ic_custom_bank_s)
        ),
        category = dummyCategoryUi(
            name = "Tech",
            color = Blue2Dark,
            icon = dummyIconUnknown(R.drawable.ic_vue_edu_telescope)
        ),
        value = dummyValueUi("55.23"),
        type = TransactionType.Expense,
    ),
)
// endregion