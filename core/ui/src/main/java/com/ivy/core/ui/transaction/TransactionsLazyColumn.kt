package com.ivy.core.ui.transaction

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.common.dateNowUTC
import com.ivy.common.timeNowUTC
import com.ivy.core.functions.account.dummyAcc
import com.ivy.core.functions.category.dummyCategory
import com.ivy.core.functions.icon.dummyIconSized
import com.ivy.core.functions.icon.dummyIconUnknown
import com.ivy.core.functions.transaction.dummyActual
import com.ivy.core.functions.transaction.dummyDue
import com.ivy.core.functions.transaction.dummyTrn
import com.ivy.core.functions.transaction.dummyValue
import com.ivy.core.ui.temp.Preview
import com.ivy.data.transaction.*
import com.ivy.design.l0_system.*
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
 * @param emptyState _(optional)_ empty state title and message.
 * @param upcomingHandler _(optional)_ custom expand/collapse handling for the "Upcoming" section.
 * @param overdueHandler _(optional)_ custom expand/collapse handling for the "Overdue" section.
 * @param trnItemClickHandler _(optional)_ handling for
 * transaction click, category & account click in trn item.
 */
@Composable
fun TransactionsList.TrnsLazyColumn(
    modifier: Modifier = Modifier,
    scrollStateKey: String?,
    dueActions: DueActions? = null,
    contentAboveTrns: (LazyListScope.(LazyListState) -> Unit)? = null,
    contentBelowTrns: (LazyListScope.(LazyListState) -> Unit)? = null,
    emptyState: EmptyState = defaultEmptyState(),
    upcomingHandler: ExpandCollapseHandler = defaultExpandCollapseHandler(),
    overdueHandler: ExpandCollapseHandler = defaultExpandCollapseHandler(),
    trnItemClickHandler: TrnItemClickHandler = defaultTrnItemClickHandler(),
) {
    val state = rememberLazyListState(
        initialFirstVisibleItemIndex =
        lazyStateCache[scrollStateKey]?.firstVisibleItemIndex ?: 0,
        initialFirstVisibleItemScrollOffset =
        lazyStateCache[scrollStateKey]?.firstVisibleItemScrollOffset ?: 0
    )

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

// region Previews
@Preview
@Composable
private fun Preview_Full() {
    Preview {
        val trnsList = TransactionsList(
            upcoming = UpcomingSection(
                income = dummyValue(16.99),
                expense = dummyValue(0.0),
                trns = listOf(
                    dummyTrn(
                        title = "Upcoming payment",
                        account = dummyAcc(
                            name = "Revolut",
                            color = Purple.toArgb(),
                            icon = dummyIconSized(R.drawable.ic_custom_revolut_s)
                        ),
                        category = dummyCategory(
                            name = "Investments",
                            color = Blue2Light.toArgb(),
                            icon = dummyIconSized(R.drawable.ic_custom_leaf_s)
                        ),
                        amount = 16.99,
                        type = TransactionType.Income,
                        time = dummyDue(timeNowUTC().plusDays(1))
                    )
                )
            ),
            overdue = OverdueSection(
                income = dummyValue(0.0),
                expense = dummyValue(650.0),
                trns = listOf(
                    dummyTrn(
                        title = "Rent",
                        amount = 650.0,
                        account = dummyAcc(
                            name = "Cash",
                            color = Green.toArgb(),
                            icon = dummyIconUnknown(R.drawable.ic_vue_money_coins)
                        ),
                        category = null,
                        type = TransactionType.Expense,
                        time = dummyDue(timeNowUTC().minusDays(1))
                    )
                )
            ),
            history = listOf(
                TrnListItem.DateDivider(
                    date = dateNowUTC(),
                    cashflow = dummyValue(-30.0)
                ),
                TrnListItem.Trn(
                    dummyTrn(
                        title = "Food",
                        account = dummyAcc(
                            name = "Revolut",
                            color = Purple.toArgb(),
                            icon = dummyIconSized(R.drawable.ic_custom_revolut_s)
                        ),
                        category = dummyCategory(
                            name = "Order food",
                            color = Orange2.toArgb(),
                            icon = dummyIconSized(R.drawable.ic_custom_orderfood_s)
                        ),
                        amount = 30.0,
                        type = TransactionType.Expense,
                        time = dummyActual(timeNowUTC())
                    )
                ),
                TrnListItem.DateDivider(
                    date = dateNowUTC().minusDays(1),
                    cashflow = dummyValue(105.33)
                ),
                TrnListItem.Trn(
                    dummyTrn(
                        title = "Buy some cool gadgets",
                        description = "Premium tech!",
                        account = dummyAcc(
                            name = "Bank",
                            color = Red.toArgb(),
                            icon = dummyIconSized(R.drawable.ic_custom_bank_s)
                        ),
                        category = dummyCategory(
                            name = "Tech",
                            color = Blue2Dark.toArgb(),
                            icon = dummyIconUnknown(R.drawable.ic_vue_edu_telescope)
                        ),
                        amount = 55.23,
                        type = TransactionType.Expense,
                    )
                ),
                TrnListItem.Trn(
                    dummyTrn(
                        title = "Ivy Apps revenue",
                        account = dummyAcc(
                            name = "Revolut Business",
                            color = Purple2Dark.toArgb(),
                            icon = dummyIconSized(R.drawable.ic_custom_revolut_s)
                        ),
                        category = null,
                        amount = 160.53,
                        type = TransactionType.Income,
                    )
                ),
                TrnListItem.Trn(
                    dummyTrn(
                        title = "Buy some cool gadgets",
                        description = "Premium tech!",
                        account = dummyAcc(
                            name = "Bank",
                            color = Red.toArgb(),
                            icon = dummyIconSized(R.drawable.ic_custom_bank_s)
                        ),
                        category = dummyCategory(
                            name = "Tech",
                            color = Blue2Dark.toArgb(),
                            icon = dummyIconUnknown(R.drawable.ic_vue_edu_telescope)
                        ),
                        amount = 55.23,
                        type = TransactionType.Expense,
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
    Preview {
        val trnsList = TransactionsList(
            upcoming = null,
            overdue = null,
            history = emptyList()
        )

        trnsList.TrnsLazyColumn(scrollStateKey = "preview2")
    }
}
// endregion