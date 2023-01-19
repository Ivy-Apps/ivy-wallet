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
import com.ivy.core.domain.action.calculate.transaction.OverdueSectionKey
import com.ivy.core.domain.action.calculate.transaction.UpcomingSectionKey
import com.ivy.core.domain.action.calculate.transaction.toggleCollapseExpandTrnListKey
import com.ivy.core.ui.algorithm.trnhistory.data.*
import com.ivy.core.ui.transaction.handling.DueActionsHandler
import com.ivy.core.ui.transaction.handling.TrnItemClickHandler
import com.ivy.core.ui.transaction.handling.defaultDueActionsHandler
import com.ivy.core.ui.transaction.handling.defaultTrnItemClickHandler
import com.ivy.core.ui.transaction.item.*
import com.ivy.design.l0_system.UI
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

        val trnsList = sampleTransactionListUi()

        LazyColumn {
            transactionsList(
                items = trnsList,
                emptyState = emptyState,
                trnClickHandler = defaultTrnItemClickHandler(),
                dueActionsHandler = defaultDueActionsHandler(),
            )
        }
    }
}

@Preview
@Composable
private fun Preview_EmptyState() {
    IvyPreview {
        val upcomingHandler = defaultExpandCollapseHandler()
        val overdueHandler = defaultExpandCollapseHandler()
        val emptyState = defaultEmptyState()
        val trnItemClickHandler = defaultTrnItemClickHandler()

        LazyColumn {
            val trnsList = TransactionsListUi(
                upcoming = null,
                overdue = null,
                history = emptyList()
            )

            transactionsList(
                trnsList = trnsList,
                emptyState = emptyState,
                upcomingHandler = upcomingHandler,
                overdueHandler = overdueHandler,
                dueActions = dummyDueActions(),
                trnClickHandler = trnItemClickHandler,
            )
        }
    }
}
// endregion