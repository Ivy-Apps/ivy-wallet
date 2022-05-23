package com.ivy.wallet.ui

import com.ivy.frp.view.navigation.Screen
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.ui.paywall.PaywallReason
import java.util.*

object Main : Screen

object Onboarding : Screen

data class EditTransaction(
    val initialTransactionId: UUID?,
    val type: TransactionType,

    //extras
    val accountId: UUID? = null,
    val categoryId: UUID? = null
) : Screen

data class ItemStatistic(
    val accountId: UUID? = null,
    val categoryId: UUID? = null,
    val unspecifiedCategory: Boolean? = false,
    val transactionType: TransactionType? = null,
    val accountIdFilterList: List<UUID> = emptyList(),
    val transactions: List<Transaction> = emptyList()
) : Screen

data class PieChartStatistic(
    val type: TransactionType,
    val filterExcluded: Boolean = true,
    val accountList: List<UUID> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val treatTransfersAsIncomeExpense: Boolean = false
) : Screen

data class EditPlanned(
    val plannedPaymentRuleId: UUID?,
    val type: TransactionType,
    val amount: Double? = null,
    val accountId: UUID? = null,
    val categoryId: UUID? = null,
    val title: String? = null,
    val description: String? = null,
) : Screen {
    fun mandatoryFilled(): Boolean {
        return amount != null && amount > 0.0
                && accountId != null
    }
}

object BalanceScreen : Screen

object PlannedPayments : Screen

object Categories : Screen

data class Paywall(
    val paywallReason: PaywallReason?
) : Screen

object Settings : Screen

object AnalyticsReport : Screen

data class Import(
    val launchedFromOnboarding: Boolean
) : Screen

object ConnectBank : Screen

object Report : Screen

object BudgetScreen : Screen

object Loans : Screen

object Search : Screen

data class LoanDetails(
    val loanId: UUID
) : Screen

object Test : Screen

data class IvyWebView(val url: String) : Screen

object Charts : Screen
