package com.ivy.home

import com.ivy.base.data.AppBaseData
import com.ivy.base.data.BufferInfo
import com.ivy.base.data.DueSection
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.Theme
import com.ivy.data.pure.IncomeExpensePair
import com.ivy.journey.domain.CustomerJourneyCardData
import java.math.BigDecimal

data class HomeState(
    val theme: Theme,
    val name: String,

    val period: TimePeriod,
    val baseData: AppBaseData,

    val history: List<Any>,
    val stats: IncomeExpensePair,

    val balance: BigDecimal,

    val buffer: BufferInfo,

    val upcoming: DueSection,
    val overdue: DueSection,

    val customerJourneyCards: List<CustomerJourneyCardData>,
    val hideCurrentBalance: Boolean
) {
    companion object {
        fun initial(ivyWalletCtx: com.ivy.core.ui.temp.IvyWalletCtx): HomeState = HomeState(
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