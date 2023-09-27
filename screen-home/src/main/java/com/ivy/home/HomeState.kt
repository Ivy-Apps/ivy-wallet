package com.ivy.home

import androidx.compose.runtime.Immutable
import com.ivy.base.legacy.TransactionHistoryItem
import com.ivy.base.model.Theme
import com.ivy.home.customerjourney.CustomerJourneyCardModel
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.data.AppBaseData
import com.ivy.legacy.data.BufferInfo
import com.ivy.legacy.data.DueSection
import com.ivy.legacy.data.model.TimePeriod
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.math.BigDecimal

@Immutable
data class HomeState(
    val theme: Theme,
    val name: String,

    val period: TimePeriod,
    val baseData: AppBaseData,

    val history: ImmutableList<TransactionHistoryItem>,
    val stats: IncomeExpensePair,

    val balance: BigDecimal,

    val buffer: BufferInfo,

    val upcoming: DueSection,
    val overdue: DueSection,

    val customerJourneyCards: ImmutableList<CustomerJourneyCardModel>,
    val hideCurrentBalance: Boolean
) {
    companion object {
        fun initial(ivyWalletCtx: IvyWalletCtx): HomeState = HomeState(
            theme = Theme.AUTO,
            name = "",
            baseData = AppBaseData(
                baseCurrency = "",
                accounts = persistentListOf(),
                categories = persistentListOf()
            ),
            balance = BigDecimal.ZERO,
            buffer = BufferInfo(
                amount = BigDecimal.ZERO,
                bufferDiff = BigDecimal.ZERO,
            ),
            customerJourneyCards = persistentListOf(),
            history = persistentListOf(),
            stats = IncomeExpensePair.zero(),
            upcoming = DueSection(
                trns = persistentListOf(),
                stats = IncomeExpensePair.zero(),
                expanded = false,
            ),
            overdue = DueSection(
                trns = persistentListOf(),
                stats = IncomeExpensePair.zero(),
                expanded = false,
            ),
            period = ivyWalletCtx.selectedPeriod,
            hideCurrentBalance = false
        )
    }
}
