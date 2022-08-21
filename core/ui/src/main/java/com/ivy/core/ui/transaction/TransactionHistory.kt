package com.ivy.core.ui.transaction

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.functions.transaction.dummyValue
import com.ivy.core.ui.temp.Preview
import com.ivy.data.transaction.*
import com.ivy.design.l1_buildingBlocks.SpacerVer

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

fun LazyListScope.transactionsList(
    trnsList: TransactionsList,
    upcomingHandler: ExpandCollapseHandler,
    overdueHandler: ExpandCollapseHandler,
    dueActions: DueActions?
) {
    upcoming(upcoming = trnsList.upcoming, handler = upcomingHandler, dueActions = dueActions)
    overdue(overdue = trnsList.overdue, handler = overdueHandler, dueActions = dueActions)
    history(history = trnsList.history)
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

// region Previews
@Preview
@Composable
private fun Preview() {
    Preview {
        val upcomingHandler = defaultExpandCollapseHandler()
        val overdueHandler = defaultExpandCollapseHandler()

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
                upcomingHandler = upcomingHandler,
                overdueHandler = overdueHandler,
                dueActions = dummyDueActions()
            )
        }
    }
}
// endregion