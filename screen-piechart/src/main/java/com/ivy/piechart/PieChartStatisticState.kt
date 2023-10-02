package com.ivy.piechart

import com.ivy.base.legacy.Transaction
import com.ivy.base.model.TransactionType
import com.ivy.legacy.data.model.TimePeriod
import com.ivy.wallet.ui.theme.modal.ChoosePeriodModalData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

data class PieChartStatisticState(
    val transactionType: TransactionType = TransactionType.INCOME,
    val period: TimePeriod = TimePeriod(),
    val baseCurrency: String = "",
    val totalAmount: Double = 0.0,
    val categoryAmounts: ImmutableList<CategoryAmount> = persistentListOf(),
    val selectedCategory: SelectedCategory? = null,
    val accountIdFilterList: ImmutableList<UUID> = persistentListOf(),
    val showCloseButtonOnly: Boolean = false,
    val filterExcluded: Boolean = false,
    val transactions: ImmutableList<Transaction> = persistentListOf(),
    val choosePeriodModal: ChoosePeriodModalData? = null
)
