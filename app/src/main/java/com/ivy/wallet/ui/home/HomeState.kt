package com.ivy.wallet.ui.home

import com.ivy.design.l0_system.Theme
import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.deprecated.logic.model.CustomerJourneyCardData
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.data.AppBaseData
import com.ivy.wallet.ui.data.BufferInfo
import com.ivy.wallet.ui.data.DueSection
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import java.math.BigDecimal

data class HomeState(
    val theme: Theme,
    val name: String,

    val period: TimePeriod,
    val baseData: AppBaseData,

    val history: List<TransactionHistoryItem>,
    val stats: IncomeExpensePair,

    val balance: BigDecimal,

    val buffer: BufferInfo,

    val upcoming: DueSection,
    val overdue: DueSection,

    val customerJourneyCards: List<CustomerJourneyCardData>,
    val hideCurrentBalance: Boolean
) {
    companion object {
        fun initial(ivyWalletCtx: IvyWalletCtx): HomeState = HomeState(
            theme = Theme.AUTO,
            name = "",
            baseData = AppBaseData(
                baseCurrency = "",
                accounts = emptyList(),
                categories = emptyList()
            ),
            balance = BigDecimal.ZERO,
            buffer = BufferInfo(
                amount = BigDecimal.ZERO,
                bufferDiff = BigDecimal.ZERO,
            ),
            customerJourneyCards = emptyList(),
            history = emptyList(),
            stats = IncomeExpensePair.zero(),
            upcoming = DueSection(
                trns = emptyList(),
                stats = IncomeExpensePair.zero(),
                expanded = false,
            ),
            overdue = DueSection(
                trns = emptyList(),
                stats = IncomeExpensePair.zero(),
                expanded = false,
            ),
            period = ivyWalletCtx.selectedPeriod,
            hideCurrentBalance = false
        )
    }
}