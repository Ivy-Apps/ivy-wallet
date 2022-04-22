package com.ivy.wallet.ui.home

import com.ivy.design.l0_system.Theme
import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.entity.Account
import com.ivy.wallet.domain.data.entity.Category
import com.ivy.wallet.domain.data.entity.Transaction
import com.ivy.wallet.domain.logic.model.CustomerJourneyCardData
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import java.math.BigDecimal

data class HomeState(
    val theme: Theme,
    val name: String,
    val baseCurrencyCode: String,
    val period: TimePeriod,
    val accounts: List<Account>,
    val categories: List<Category>,
    val balance: BigDecimal,
    val buffer: BigDecimal,
    val bufferDiff: BigDecimal,
    val monthly: IncomeExpensePair,
    val upcoming: IncomeExpensePair,
    val upcomingTrns: List<Transaction>,
    val upcomingExpanded: Boolean,
    val overdue: IncomeExpensePair,
    val overdueTrns: List<Transaction>,
    val overdueExpanded: Boolean,
    val history: List<TransactionHistoryItem>,
    val customerJourneyCards: List<CustomerJourneyCardData>,
    val hideCurrentBalance: Boolean
) {
    companion object {
        fun initial(ivyWalletCtx: IvyWalletCtx): HomeState = HomeState(
            theme = Theme.AUTO,
            name = "",
            accounts = emptyList(),
            categories = emptyList(),
            baseCurrencyCode = "",
            balance = BigDecimal.ZERO,
            buffer = BigDecimal.ZERO,
            bufferDiff = BigDecimal.ZERO,
            customerJourneyCards = emptyList(),
            history = emptyList(),
            monthly = IncomeExpensePair.zero(),
            upcoming = IncomeExpensePair.zero(),
            upcomingTrns = emptyList(),
            upcomingExpanded = false,
            overdue = IncomeExpensePair.zero(),
            overdueTrns = emptyList(),
            overdueExpanded = false,
            period = ivyWalletCtx.selectedPeriod,
            hideCurrentBalance = false
        )
    }
}