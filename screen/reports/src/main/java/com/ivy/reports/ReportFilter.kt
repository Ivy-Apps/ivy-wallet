package com.ivy.reports

import com.ivy.base.model.TransactionType
import com.ivy.data.model.Category
import com.ivy.data.model.Tag
import com.ivy.legacy.data.model.TimePeriod
import com.ivy.legacy.datamodel.Account
import java.util.UUID

data class ReportFilter(
    val id: UUID = UUID.randomUUID(),
    val trnTypes: List<TransactionType>,
    val period: TimePeriod?,
    val accounts: List<Account>,
    val categories: List<Category>,
    val currency: String,
    val minAmount: Double?,
    val maxAmount: Double?,
    val includeKeywords: List<String>,
    val excludeKeywords: List<String>,
    val selectedTags: List<Tag>
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
            maxAmount = null,
            selectedTags = emptyList()
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
