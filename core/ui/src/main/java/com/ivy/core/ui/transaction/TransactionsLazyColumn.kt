package com.ivy.core.ui.transaction

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.algorithm.trnhistory.data.TrnListItemUi
import com.ivy.core.ui.transaction.handling.DueActionsHandler
import com.ivy.core.ui.transaction.handling.TrnItemClickHandler
import com.ivy.core.ui.transaction.handling.defaultDueActionsHandler
import com.ivy.core.ui.transaction.handling.defaultTrnItemClickHandler
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.util.IvyPreview

private var lazyStateCache: MutableMap<String, LazyListState> = mutableMapOf()

@Stable
data class TransactionsListState internal constructor(
    val scrollStateKey: String?,
    val listState: LazyListState,
)

/**
 * @param scrollStateKey an **unique key** by which the `LazyListState` is cached
 * so scroll progress is persisted. Set to **null** if you don't want to
 * persist scroll state.
 */
@Composable
fun rememberTransactionsListState(scrollStateKey: String?) = TransactionsListState(
    scrollStateKey = scrollStateKey,
    listState = rememberLazyListState(
        initialFirstVisibleItemIndex =
        lazyStateCache[scrollStateKey]?.firstVisibleItemIndex ?: 0,
        initialFirstVisibleItemScrollOffset =
        lazyStateCache[scrollStateKey]?.firstVisibleItemScrollOffset ?: 0
    )
)


/**
 * Displays a list of transactions _(Upcoming, Overdue & History)_ efficiently in a **LazyColumn**.
 * Optionally, **persists scroll progress** so when the user navigates back to the screen
 * the list state is the same as the user left it.
 *
 * @param modifier a Modifier for the LazyColumn.
 * @param state use [rememberTransactionsListState]
 * @param dueActionsHandler _(optional)_ skip or pay/get planned payment callbacks,
 * if null "Skip" and "Pay"/"Get" buttons won't be shown.
 * @param contentAboveTrns _(optional)_ LazyColumn items above the transactions list.
 * @param contentBelowTrns _(optional)_ LazyColum items below the transactions list.
 * Defaults to 300.dp spacer so the transactions list can be scrollable.
 * @param emptyState _(optional)_ empty state title and message.
 * @param trnItemClickHandler _(optional)_ custom handling for
 * transaction click, category click and account click events for a transaction item in the list.
 */
@Composable
fun TransactionsLazyColumn(
    items: List<TrnListItemUi>,
    state: TransactionsListState,
    modifier: Modifier = Modifier,
    contentAboveTrns: (LazyListScope.(LazyListState) -> Unit)? = null,
    contentBelowTrns: (LazyListScope.(LazyListState) -> Unit)? = { scrollingSpace() },
    emptyState: EmptyState = defaultEmptyState(),
    dueActionsHandler: DueActionsHandler = defaultDueActionsHandler(),
    trnItemClickHandler: TrnItemClickHandler = defaultTrnItemClickHandler(),
    onFirstVisibleItemChange: (suspend (Int) -> Unit)? = null,
) {
    if (onFirstVisibleItemChange != null) {
        val firstVisibleItemIndex by remember {
            derivedStateOf { state.listState.firstVisibleItemIndex }
        }

        LaunchedEffect(firstVisibleItemIndex) {
            onFirstVisibleItemChange(firstVisibleItemIndex)
        }
    }

    if (state.scrollStateKey != null) {
        // Cache scrolling state
        DisposableEffect(state.scrollStateKey) {
            lazyStateCache[state.scrollStateKey] = state.listState
            onDispose {}
        }
    }

    LazyColumn(
        modifier = modifier,
        state = state.listState
    ) {
        contentAboveTrns?.invoke(this, state.listState)
        transactionsList(
            items = items,
            emptyState = emptyState,
            dueActionsHandler = dueActionsHandler,
            trnClickHandler = trnItemClickHandler,
        )
        contentBelowTrns?.invoke(this, state.listState)
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
    IvyPreview {
        TransactionsLazyColumn(
            items = sampleTrnListItems(),
            state = rememberTransactionsListState(scrollStateKey = "preview1")
        )
    }
}

@Preview
@Composable
private fun Preview_EmptyState() {
    IvyPreview {
        TransactionsLazyColumn(
            items = emptyList(),
            state = rememberTransactionsListState(scrollStateKey = "preview2")
        )
    }
}
// endregion