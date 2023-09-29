package com.ivy.navigation

import com.ivy.base.legacy.Transaction
import com.ivy.base.model.TransactionType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

data object MainScreen : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object OnboardingScreen : Screen {
    override val isLegacy: Boolean
        get() = true
}

data class CSVScreen(
    val launchedFromOnboarding: Boolean
) : Screen {
    override val isLegacy: Boolean
        get() = true
}

data class EditTransactionScreen(
    val initialTransactionId: UUID?,
    val type: TransactionType,
    // extras
    val accountId: UUID? = null,
    val categoryId: UUID? = null
) : Screen {
    override val isLegacy: Boolean
        get() = true
}

data class TransactionsScreen(
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
) : Screen {
    override val isLegacy: Boolean
        get() = true
}

data class EditPlannedScreen(
    val plannedPaymentRuleId: UUID?,
    val type: TransactionType,
    val amount: Double? = null,
    val accountId: UUID? = null,
    val categoryId: UUID? = null,
    val title: String? = null,
    val description: String? = null,
) : Screen {
    override val isLegacy: Boolean
        get() = true

    fun mandatoryFilled(): Boolean {
        return amount != null && amount > 0.0 &&
                accountId != null
    }
}

data object BalanceScreen : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object PlannedPaymentsScreen : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object CategoriesScreen : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object SettingsScreen : Screen {
    override val isLegacy: Boolean
        get() = true
}

data class ImportScreen(
    val launchedFromOnboarding: Boolean
) : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object ReportScreen : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object BudgetScreen : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object LoansScreen : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object SearchScreen : Screen {
    override val isLegacy: Boolean
        get() = true
}

data class LoanDetailsScreen(
    val loanId: UUID
) : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object ExchangeRatesScreen : Screen {
    override val isLegacy: Boolean
        get() = true
}

data object FeaturesScreen : Screen

data object AttributionsScreen : Screen

data object ContributorsScreen : Screen

data object ReleasesScreen : Screen