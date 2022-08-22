package com.ivy.reports

import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import com.ivy.data.transaction.TrnType
import java.util.*

data class ReportFilter(
    val id: UUID = UUID.randomUUID(),
    val trnTypes: List<TrnType>,
    val period: TimePeriod?,
    val accounts: List<AccountOld>,
    val categories: List<CategoryOld>,
    val currency: String,
    val minAmount: Double?,
    val maxAmount: Double?,
    val includeKeywords: List<String>,
    val excludeKeywords: List<String>
) {
    companion object {
        fun emptyFilter(
            baseCurrency: String
        ) = ReportFilter(
            trnTypes = emptyList(),
            period = null,
            accounts = emptyList(),
            categories = emptyList(),
            currency = baseCurrency,
            includeKeywords = emptyList(),
            excludeKeywords = emptyList(),
            minAmount = null,
            maxAmount = null
        )
    }

    fun validate(): Boolean {
        if (trnTypes.isEmpty()) return false

        if (period == null) return false

        if (accounts.isEmpty()) return false

        if (categories.isEmpty()) return false

        if (minAmount != null && maxAmount != null) {
            if (minAmount > maxAmount) return false
            if (maxAmount < minAmount) return false
        }

        return true
    }
}