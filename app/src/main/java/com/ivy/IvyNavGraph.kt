package com.ivy

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import com.ivy.attributions.AttributionsScreenImpl
import com.ivy.balance.BalanceScreen
import com.ivy.budgets.BudgetScreen
import com.ivy.categories.CategoriesScreen
import com.ivy.contributors.ContributorsScreenImpl
import com.ivy.exchangerates.ExchangeRatesScreen
import com.ivy.features.FeaturesScreenImpl
import com.ivy.importdata.csv.CSVScreen
import com.ivy.importdata.csvimport.ImportCSVScreen
import com.ivy.loans.loan.LoansScreen
import com.ivy.loans.loandetails.LoanDetailsScreen
import com.ivy.main.MainScreen
import com.ivy.navigation.AttributionsScreen
import com.ivy.navigation.BalanceScreen
import com.ivy.navigation.BudgetScreen
import com.ivy.navigation.CSVScreen
import com.ivy.navigation.CategoriesScreen
import com.ivy.navigation.ContributorsScreen
import com.ivy.navigation.EditPlannedScreen
import com.ivy.navigation.EditTransactionScreen
import com.ivy.navigation.ExchangeRatesScreen
import com.ivy.navigation.FeaturesScreen
import com.ivy.navigation.ImportScreen
import com.ivy.navigation.LoanDetailsScreen
import com.ivy.navigation.LoansScreen
import com.ivy.navigation.MainScreen
import com.ivy.navigation.OnboardingScreen
import com.ivy.navigation.PieChartStatisticScreen
import com.ivy.navigation.PlannedPaymentsScreen
import com.ivy.navigation.ReleasesScreen
import com.ivy.navigation.ReportScreen
import com.ivy.navigation.Screen
import com.ivy.navigation.SearchScreen
import com.ivy.navigation.SettingsScreen
import com.ivy.navigation.TransactionsScreen
import com.ivy.onboarding.OnboardingScreen
import com.ivy.piechart.PieChartStatisticScreen
import com.ivy.planned.edit.EditPlannedScreen
import com.ivy.planned.list.PlannedPaymentsScreen
import com.ivy.releases.ReleasesScreenImpl
import com.ivy.reports.ReportScreen
import com.ivy.search.SearchScreen
import com.ivy.settings.SettingsScreen
import com.ivy.transaction.EditTransactionScreen
import com.ivy.transactions.TransactionsScreen

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun BoxWithConstraintsScope.IvyNavGraph(screen: Screen?) {
    when (screen) {
        null -> {
            // show nothing
        }

        is MainScreen -> MainScreen(screen = screen)
        is OnboardingScreen -> OnboardingScreen(screen = screen)
        is ExchangeRatesScreen -> ExchangeRatesScreen()
        is EditTransactionScreen -> EditTransactionScreen(screen = screen)
        is TransactionsScreen -> TransactionsScreen(screen = screen)
        is PieChartStatisticScreen -> PieChartStatisticScreen(screen = screen)
        is CategoriesScreen -> CategoriesScreen(screen = screen)
        is SettingsScreen -> SettingsScreen()
        is PlannedPaymentsScreen -> PlannedPaymentsScreen(screen = screen)
        is EditPlannedScreen -> EditPlannedScreen(screen = screen)
        is BalanceScreen -> BalanceScreen(screen = screen)
        is ImportScreen -> ImportCSVScreen(screen = screen)
        is ReportScreen -> ReportScreen(screen = screen)
        is BudgetScreen -> BudgetScreen(screen = screen)
        is LoansScreen -> LoansScreen(screen = screen)
        is LoanDetailsScreen -> LoanDetailsScreen(screen = screen)
        is SearchScreen -> SearchScreen(screen = screen)
        is CSVScreen -> CSVScreen(screen = screen)
        FeaturesScreen -> FeaturesScreenImpl()
        AttributionsScreen -> AttributionsScreenImpl()
        ContributorsScreen -> ContributorsScreenImpl()
        ReleasesScreen -> ReleasesScreenImpl()
    }
}