package com.ivy.navigation

import com.ivy.core.db.entity.TransactionType
import com.ivy.core.datamodel.Transaction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

data object MainScreen : Screen

data object OnboardingScreen : Screen

data class CSVScreen(
    val launchedFromOnboarding: Boolean
) : Screen

data class EditTransactionScreen(
    val initialTransactionId: UUID?,
    val type: TransactionType,
    // extras
    val accountId: UUID? = null,
    val categoryId: UUID? = null
) : Screen

data class ItemStatisticScreen(
    val accountId: UUID? = null,
    val categoryId: UUID? = null,
    val unspecifiedCategory: Boolean? = false,
    val transactionType: TransactionType? = null,
    val accountIdFilterList: List<UUID> = persistentListOf(),
    val transactions: List<Transaction> = persistentListOf()
) : Screen

data class PieChartStatisticScreen(
    val type: TransactionType,
    val filterExcluded: Boolean = true,
    val accountList: ImmutableList<UUID> = persistentListOf(),
    val transactions: ImmutableList<Transaction> = persistentListOf(),
    val treatTransfersAsIncomeExpense: Boolean = false
) : Screen

data class EditPlannedScreen(
    val plannedPaymentRuleId: UUID?,
    val type: TransactionType,
    val amount: Double? = null,
    val accountId: UUID? = null,
    val categoryId: UUID? = null,
    val title: String? = null,
    val description: String? = null,
) : Screen {
    fun mandatoryFilled(): Boolean {
        return amount != null && amount > 0.0 &&
                accountId != null
    }
}

data object BalanceScreen : Screen

data object PlannedPaymentsScreen : Screen

data object CategoriesScreen : Screen

data object SettingsScreen : Screen

data class ImportScreen(
    val launchedFromOnboarding: Boolean
) : Screen

data object ReportScreen : Screen

data object BudgetScreen : Screen

data object LoansScreen : Screen

data object SearchScreen : Screen

data class LoanDetailsScreen(
    val loanId: UUID
) : Screen

data object TestScreen : Screen

data object ExchangeRatesScreen : Screen

data object FeaturesScreen : Screen