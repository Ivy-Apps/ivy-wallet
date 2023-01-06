package com.ivy.core.ui.transaction

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.common.time.timeNow
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.icon.dummyIconSized
import com.ivy.core.ui.data.icon.dummyIconUnknown
import com.ivy.core.ui.data.transaction.*
import com.ivy.core.ui.transaction.card.DueActions
import com.ivy.core.ui.transaction.handling.ExpandCollapseHandler
import com.ivy.core.ui.transaction.handling.TrnItemClickHandler
import com.ivy.core.ui.transaction.handling.defaultExpandCollapseHandler
import com.ivy.core.ui.transaction.handling.defaultTrnItemClickHandler
import com.ivy.data.transaction.TransactionType
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
 * @param trnItemClickHandler _(optional)_ custom handling for
 * transaction click, category click and account click events for a transaction item in the list.
 */
@Composable
fun TransactionsLazyColumn(
    transactionsList: TransactionsListUi,
    scrollStateKey: String?,
    modifier: Modifier = Modifier,
    dueActions: DueActions? = null,
    contentAboveTrns: (LazyListScope.(LazyListState) -> Unit)? = null,
    contentBelowTrns: (LazyListScope.(LazyListState) -> Unit)? = { scrollingSpace() },
    emptyState: EmptyState = defaultEmptyState(),
    upcomingHandler: ExpandCollapseHandler = defaultExpandCollapseHandler(),
    overdueHandler: ExpandCollapseHandler = defaultExpandCollapseHandler(),
    trnItemClickHandler: TrnItemClickHandler = defaultTrnItemClickHandler(),
    onFirstVisibleItemChange: (suspend (Int) -> Unit)? = null,
) {
    val state = rememberLazyListState(
        initialFirstVisibleItemIndex =
        lazyStateCache[scrollStateKey]?.firstVisibleItemIndex ?: 0,
        initialFirstVisibleItemScrollOffset =
        lazyStateCache[scrollStateKey]?.firstVisibleItemScrollOffset ?: 0
    )

    if (onFirstVisibleItemChange != null) {
        val firstVisibleItemIndex by remember {
            derivedStateOf { state.firstVisibleItemIndex }
        }

        LaunchedEffect(firstVisibleItemIndex) {
            onFirstVisibleItemChange(firstVisibleItemIndex)
        }
    }

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
            trnsList = transactionsList,
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
        val trnsList = sampleTransactionListUi()

        TransactionsLazyColumn(
            transactionsList = trnsList,
            scrollStateKey = "preview1"
        )
    }
}

@Composable
fun sampleTransactionListUi(): TransactionsListUi = TransactionsListUi(
    upcoming = DueSectionUi(
        dueType = DueSectionUiType.Upcoming,
        income = dummyValueUi("16.99"),
        expense = null,
        trns = listOf(
            TrnListItemUi.Trn(
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
                )
            )
        )
    ),
    overdue = DueSectionUi(
        dueType = DueSectionUiType.Overdue,
        income = null,
        expense = dummyValueUi("650.0"),
        trns = listOf(
            TrnListItemUi.Trn(
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
                )
            )
        )
    ),
    history = listOf(
        TrnListItemUi.DateDivider(
            id = "2021-01-01",
            date = "September 25.",
            day = "Friday",
            cashflow = dummyValueUi("-30.0"),
            positiveCashflow = false,
            collapsed = false,
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
                value = dummyValueUi("30.0"),
                type = TransactionType.Expense,
                time = dummyTrnTimeActualUi()
            )
        ),
        TrnListItemUi.DateDivider(
            id = "2021-01-01",
            date = "September 23.",
            day = "Wednesday",
            cashflow = dummyValueUi("105.33"),
            positiveCashflow = true,
            collapsed = false,
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
                value = dummyValueUi("55.23"),
                type = TransactionType.Expense,
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
                value = dummyValueUi("160.53"),
                type = TransactionType.Income,
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
                value = dummyValueUi("55.23"),
                type = TransactionType.Expense,
            )
        ),
    )
)

@Preview
@Composable
private fun Preview_EmptyState() {
    IvyPreview {
        val trnsList = TransactionsListUi(
            upcoming = null,
            overdue = null,
            history = emptyList()
        )

        TransactionsLazyColumn(
            transactionsList = trnsList,
            scrollStateKey = "preview2"
        )
    }
}
// endregion