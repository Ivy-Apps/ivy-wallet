package com.ivy.core.ui.transaction

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.common.timeNowUTC
import com.ivy.core.domain.pure.format.dummyFormattedValue
import com.ivy.core.ui.data.dummyAccountUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.icon.dummyIconSized
import com.ivy.core.ui.data.icon.dummyIconUnknown
import com.ivy.core.ui.data.transaction.*
import com.ivy.core.ui.transaction.card.DueActions
import com.ivy.data.transaction.TrnType
import com.ivy.design.l0_system.color.*
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.util.IvyPreview
import com.ivy.resources.R

private var lazyStateCache: MutableMap<String, LazyListState> = mutableMapOf()

/**
 * Displays a list of transactions _(Upcoming, Overdue & History)_ efficiently in a **LazyColumn**.
 * Optionally, **persists scroll progress** so when the user navigates back to the screen
 * the list state is the same as the user left it.
 *
 * @param modifier a Modifier for the LazyColumn.
 * @param scrollStateKey an **unique key** by which the `LazyListState` is cached
 * so scroll progress is persisted. Set to **null** if you don't want to
 * persist scroll state.
 * @param dueActions _(optional)_ skip or pay/get planned payment callbacks,
 * if null "Skip" and "Pay"/"Get" buttons won't be shown.
 * @param contentAboveTrns _(optional)_ LazyColumn items above the transactions list.
 * @param contentBelowTrns _(optional)_ LazyColum items below the transactions list.
 * Defaults to 300.dp spacer so the transactions list can be scrollable.
 * @param emptyState _(optional)_ empty state title and message.
 * @param upcomingHandler _(optional)_ custom expand/collapse handling for the "Upcoming" section.
 * @param overdueHandler _(optional)_ custom expand/collapse handling for the "Overdue" section.
 * @param trnItemClickHandler _(optional)_ handling for
 * transaction click, category click and account click events for a transaction item in the list.
 */
@Composable
fun TransactionsListUi.TrnsLazyColumn(
    modifier: Modifier = Modifier,
    scrollStateKey: String?,
    dueActions: DueActions? = null,
    contentAboveTrns: (LazyListScope.(LazyListState) -> Unit)? = null,
    contentBelowTrns: (LazyListScope.(LazyListState) -> Unit)? = { scrollingSpace() },
    emptyState: EmptyState = defaultEmptyState(),
    upcomingHandler: ExpandCollapseHandler = defaultExpandCollapseHandler(),
    overdueHandler: ExpandCollapseHandler = defaultExpandCollapseHandler(),
    trnItemClickHandler: TrnItemClickHandler = dummyTrnItemClickHandler(),
) {
    val state = rememberLazyListState(
        initialFirstVisibleItemIndex =
        lazyStateCache[scrollStateKey]?.firstVisibleItemIndex ?: 0,
        initialFirstVisibleItemScrollOffset =
        lazyStateCache[scrollStateKey]?.firstVisibleItemScrollOffset ?: 0
    )

    if (scrollStateKey != null) {
        // Cache scrolling state
        DisposableEffect(key1 = scrollStateKey) {
            lazyStateCache[scrollStateKey] = state
            onDispose {}
        }
    }

    LazyColumn(
        modifier = modifier,
        state = state
    ) {
        contentAboveTrns?.invoke(this, state)
        transactionsList(
            trnsList = this@TrnsLazyColumn,
            emptyState = emptyState,
            upcomingHandler = upcomingHandler,
            overdueHandler = overdueHandler,
            dueActions = dueActions,
            trnClickHandler = trnItemClickHandler,
        )
        contentBelowTrns?.invoke(this, state)
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
        val trnsList = TransactionsListUi(
            upcoming = DueSectionUi(
                dueType = DueSectionUiType.Upcoming,
                income = dummyFormattedValue("16.99"),
                expense = null,
                trns = listOf(
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
                        value = dummyFormattedValue("16.99"),
                        type = TrnType.Income,
                        time = dummyTrnTimeDueUi(timeNowUTC().plusDays(1))
                    )
                )
            ),
            overdue = DueSectionUi(
                dueType = DueSectionUiType.Overdue,
                income = null,
                expense = dummyFormattedValue("650.0"),
                trns = listOf(
                    dummyTransactionUi(
                        title = "Rent",
                        value = dummyFormattedValue("650.0"),
                        account = dummyAccountUi(
                            name = "Cash",
                            color = Green,
                            icon = dummyIconUnknown(R.drawable.ic_vue_money_coins)
                        ),
                        category = null,
                        type = TrnType.Expense,
                        time = dummyTrnTimeDueUi()
                    )
                )
            ),
            history = listOf(
                TrnListItemUi.DateDivider(
                    date = "September 25.",
                    day = "Friday",
                    cashflow = dummyFormattedValue("-30.0"),
                    positiveCashflow = false
                ),
                TrnListItemUi.Trn(
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
                        value = dummyFormattedValue("30.0"),
                        type = TrnType.Expense,
                        time = dummyTrnTimeActualUi()
                    )
                ),
                TrnListItemUi.DateDivider(
                    date = "September 23.",
                    day = "Wednesday",
                    cashflow = dummyFormattedValue("105.33"),
                    positiveCashflow = true
                ),
                TrnListItemUi.Trn(
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
                        value = dummyFormattedValue("55.23"),
                        type = TrnType.Expense,
                    )
                ),
                TrnListItemUi.Trn(
                    dummyTransactionUi(
                        title = "Ivy Apps revenue",
                        account = dummyAccountUi(
                            name = "Revolut Business",
                            color = Purple2Dark,
                            icon = dummyIconSized(R.drawable.ic_custom_revolut_s)
                        ),
                        category = null,
                        value = dummyFormattedValue("160.53"),
                        type = TrnType.Income,
                    )
                ),
                TrnListItemUi.Trn(
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
                        value = dummyFormattedValue("55.23"),
                        type = TrnType.Expense,
                    )
                ),
            )
        )

        trnsList.TrnsLazyColumn(scrollStateKey = "preview1")
    }
}

@Preview
@Composable
private fun Preview_EmptyState() {
    IvyPreview {
        val trnsList = TransactionsListUi(
            upcoming = null,
            overdue = null,
            history = emptyList()
        )

        trnsList.TrnsLazyColumn(scrollStateKey = "preview2")
    }
}
// endregion