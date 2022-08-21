package com.ivy.core.ui.transaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.functions.transaction.dummyValue
import com.ivy.core.ui.temp.Preview
import com.ivy.data.transaction.*
import com.ivy.design.l0_system.Gray
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.IvyIcon
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.resources.R

// region Expand & Collapse Handling
data class ExpandCollapseHandler(
    val expanded: Boolean,
    val setExpanded: (Boolean) -> Unit,
)

@Composable
fun defaultExpandCollapseHandler(): ExpandCollapseHandler {
    var expanded by remember { mutableStateOf(false) }
    return ExpandCollapseHandler(
        expanded = expanded,
        setExpanded = { expanded = it }
    )
}
// endregion

// region EmptyState data
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

fun LazyListScope.transactionsList(
    trnsList: TransactionsList,
    emptyState: EmptyState,
    upcomingHandler: ExpandCollapseHandler,
    overdueHandler: ExpandCollapseHandler,
    dueActions: DueActions?
) {
    upcoming(upcoming = trnsList.upcoming, handler = upcomingHandler, dueActions = dueActions)
    overdue(overdue = trnsList.overdue, handler = overdueHandler, dueActions = dueActions)
    history(history = trnsList.history)

    val isEmpty by derivedStateOf {
        trnsList.history.isEmpty() && trnsList.upcoming == null && trnsList.overdue == null
    }
    if (isEmpty) {
        emptyState(emptyState)
    }

    scrollingSpace()
}


private fun LazyListScope.upcoming(
    upcoming: UpcomingSection?,
    handler: ExpandCollapseHandler,
    dueActions: DueActions?
) {
    if (upcoming != null) {
        item {
            SpacerVer(height = 24.dp)
            upcoming.SectionDivider(
                expanded = handler.expanded,
                setExpanded = handler.setExpanded,
            )
        }

        if (handler.expanded) {
            dueTrns(trns = upcoming.trns, dueActions = dueActions)
        }
    }
}

private fun LazyListScope.overdue(
    overdue: OverdueSection?,
    handler: ExpandCollapseHandler,
    dueActions: DueActions?
) {
    if (overdue != null) {
        item {
            SpacerVer(height = 24.dp)
            overdue.SectionDivider(
                expanded = handler.expanded,
                setExpanded = handler.setExpanded,
            )
        }

        if (handler.expanded) {
            dueTrns(trns = overdue.trns, dueActions = dueActions)
        }
    }
}

private fun LazyListScope.dueTrns(
    trns: List<Transaction>,
    dueActions: DueActions?,
) {
    items(
        items = trns,
        key = { it.id.toString() }
    ) { trn ->
        SpacerVer(height = 12.dp)
        trn.Card(dueActions = dueActions)
    }
}

private fun LazyListScope.history(
    history: List<TrnListItem>,
) {
    itemsIndexed(
        items = history,
        key = { _, item ->
            when (item) {
                is TrnListItem.DateDivider -> item.date.toString()
                is TrnListItem.Trn -> item.trn.id.toString()
            }
        }
    ) { index, item ->
        when (item) {
            is TrnListItem.DateDivider -> {
                SpacerVer(
                    // not first date divider
                    height = if (index > 0 && history[index - 1] is TrnListItem.Trn)
                        32.dp else 24.dp
                )
                item.DateDivider()
            }
            is TrnListItem.Trn -> {
                SpacerVer(height = 12.dp)
                item.trn.Card()
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
            IvyIcon(icon = R.drawable.ic_notransactions, tint = Gray)
            SpacerVer(height = 24.dp)

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = emptyState.title,
                style = UI.typo.b1.style(
                    color = Gray,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                )
            )

            SpacerVer(height = 8.dp)

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                text = emptyState.description,
                style = UI.typo.b2.style(
                    color = Gray,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

private fun LazyListScope.scrollingSpace() {
    item {
        SpacerVer(height = 300.dp)
    }
}

// region Previews
@Preview
@Composable
private fun Preview_Full() {
    Preview {
        val upcomingHandler = defaultExpandCollapseHandler()
        val overdueHandler = defaultExpandCollapseHandler()
        val emptyState = defaultEmptyState()

        LazyColumn {
            val trnsList = TransactionsList(
                upcoming = UpcomingSection(
                    income = dummyValue(16.99),
                    expense = dummyValue(),
                    trns = listOf(

                    )
                ),
                overdue = null,
                history = emptyList()
            )

            transactionsList(
                trnsList = trnsList,
                emptyState = emptyState,
                upcomingHandler = upcomingHandler,
                overdueHandler = overdueHandler,
                dueActions = dummyDueActions()
            )
        }
    }
}

@Preview
@Composable
private fun Preview_EmptyState() {
    Preview {
        val upcomingHandler = defaultExpandCollapseHandler()
        val overdueHandler = defaultExpandCollapseHandler()
        val emptyState = defaultEmptyState()

        LazyColumn {
            val trnsList = TransactionsList(
                upcoming = null,
                overdue = null,
                history = emptyList()
            )

            transactionsList(
                trnsList = trnsList,
                emptyState = emptyState,
                upcomingHandler = upcomingHandler,
                overdueHandler = overdueHandler,
                dueActions = dummyDueActions()
            )
        }
    }
}
// endregion