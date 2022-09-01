package com.ivy.reports

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.base.R
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
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.reports.ReportsEvent.FilterOptions
import com.ivy.reports.ui.ReportsFilterOptions
import com.ivy.reports.ui.ReportsLoadingScreen
import com.ivy.reports.ui.ReportsScreenUI
import com.ivy.screens.Report

const val TAG = "ReportsUI"

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ReportScreen(
    screen: Report
) {
    val viewModel: ReportViewModel = viewModel()
    val state by viewModel.state().collectAsState()

    onScreenStart {
        viewModel.onEvent(ReportsEvent.Start)
    }

    UI(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    state: ReportState,
    onEvent: (ReportsEvent) -> Unit = {}
) {
    ReportsLoadingScreen(visible = state.loading, text = stringResource(R.string.generating_report))

    ReportsScreenUI(
        baseCurrency = state.baseCurrency,
        headerState = state.headerState,
        trnsList = state.trnsList,
        onEvent = onEvent
    )

    ReportsFilterOptions(
        baseCurrency = state.baseCurrency,
        state = state.filterState,
        onClose = {
            onEvent(FilterOptions(visible = false))
        },
        onFilterEvent = {
            onEvent(ReportsEvent.FilterEvent(it))
        }
    )
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    val accountList = listOf(
        dummyAcc(
            name = "Revolut",
            color = Purple.toArgb(),
            icon = dummyIconSized(com.ivy.resources.R.drawable.ic_custom_revolut_s)
        ),
        dummyAcc(
            name = "Cash",
            color = Green.toArgb(),
            icon = dummyIconUnknown(com.ivy.resources.R.drawable.ic_vue_money_coins)
        ),
        dummyAcc(
            name = "Bank",
            color = Red.toArgb(),
            icon = dummyIconSized(com.ivy.resources.R.drawable.ic_custom_bank_s)
        ),
        dummyAcc(
            name = "Revolut Business",
            color = Purple2Dark.toArgb(),
            icon = dummyIconSized(com.ivy.resources.R.drawable.ic_custom_revolut_s)
        )
    )

    val categoryList = listOf(
        dummyCategory(
            name = "Investments",
            color = Blue2Light.toArgb(),
            icon = dummyIconSized(com.ivy.resources.R.drawable.ic_custom_leaf_s)
        ),
        dummyCategory(
            name = "Order food",
            color = Orange2.toArgb(),
            icon = dummyIconSized(com.ivy.resources.R.drawable.ic_custom_orderfood_s)
        ),
        dummyCategory(
            name = "Tech",
            color = Blue2Dark.toArgb(),
            icon = dummyIconUnknown(com.ivy.resources.R.drawable.ic_vue_edu_telescope)
        )
    )

    val transList = TransactionsList(
        upcoming = UpcomingSection(
            income = dummyValue(16.99),
            expense = dummyValue(0.0),
            trns = listOf(
                dummyTrn(
                    title = "Upcoming payment",
                    account = accountList[0],
                    category = categoryList[0],
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
                    account = accountList[1],
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
                    account = accountList[0],
                    category = categoryList[1],
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
                    account = accountList[2],
                    category = categoryList[2],
                    amount = 55.23,
                    type = TransactionType.Expense,
                )
            ),
            TrnListItem.Trn(
                dummyTrn(
                    title = "Ivy Apps revenue",
                    account = accountList[3],
                    category = null,
                    amount = 160.53,
                    type = TransactionType.Income,
                )
            ),
            TrnListItem.Trn(
                dummyTrn(
                    title = "Buy some cool gadgets",
                    description = "Premium tech!",
                    account = accountList[2],
                    category = categoryList[2],
                    amount = 55.23,
                    type = TransactionType.Expense,
                )
            )
        )
    )

    val expense = 140.46
    val income = 160.53

    val headerState = emptyHeaderState().copy(
        balance = 75.33,
        income = income,
        expenses = expense,
        incomeTransactionsCount = 1,
        expenseTransactionsCount = 3,
    )

    val state = emptyReportScreenState(baseCurrency = "USD").copy(
        filterState = emptyFilterState().copy(
            selectedAcc = accountList.toImmutableItem(),
            selectedCat = categoryList.toImmutableItem()
        ),
        trnsList = transList.toImmutableItem(),
        headerState = headerState
    )

    Preview {
        UI(state = state)
    }
}
//
//@ExperimentalFoundationApi
//@Preview
//@Composable
//private fun Preview_NO_FILTER() {
//    com.ivy.core.ui.temp.Preview {
//        val acc1 = AccountOld("Cash", color = Green.toArgb())
//        val acc2 = AccountOld("DSK", color = GreenDark.toArgb())
//        val cat1 = CategoryOld("Science", color = Purple1Dark.toArgb(), icon = "atom")
//        val state = ReportScreenState(
//            baseCurrency = "BGN",
//            balance = 0.0,
//            income = 0.0,
//            expenses = 0.0,
//            upcomingIncome = 0.0,
//            upcomingExpenses = 0.0,
//            overdueIncome = 0.0,
//            overdueExpenses = 0.0,
//
//            history = emptyList(),
//            upcomingTransactions = emptyList(),
//            overdueTransactions = emptyList(),
//
//            upcomingExpanded = true,
//            overdueExpanded = true,
//
//            filter = null,
//            loading = false,
//
//            accounts = listOf(
//                acc1,
//                acc2,
//                AccountOld("phyre", color = GreenLight.toArgb(), icon = "cash"),
//                AccountOld("Revolut", color = IvyDark.toArgb()),
//            ),
//            categories = listOf(
//                cat1,
//                CategoryOld("Pet", color = Red3Light.toArgb(), icon = "pet"),
//                CategoryOld("Home", color = Green.toArgb(), icon = null),
//            ),
//        )
//
//        UI(state = state)
//    }
//}