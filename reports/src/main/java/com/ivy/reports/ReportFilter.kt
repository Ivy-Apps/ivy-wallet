package com.ivy.reports

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.TrnType
import kotlinx.collections.immutable.ImmutableList
import java.util.*

@Immutable
data class ReportFilter(
    val id: UUID,
    val trnTypes: List<TrnType>,
    val period: TimePeriod?,
    val accounts: ImmutableList<Account>,
    val categories: List<Category>,
    val currency: String,
    val minAmount: Double?,
    val maxAmount: Double?,
    val includeKeywords: List<String>,
    val excludeKeywords: List<String>,
)