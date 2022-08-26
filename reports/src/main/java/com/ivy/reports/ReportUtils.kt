package com.ivy.reports

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ivy.core.ui.transaction.EmptyState
import com.ivy.core.ui.transaction.ExpandCollapseHandler
import com.ivy.data.transaction.TransactionsList

data class AmountModalState(val amount: Double?, val onSetAmount: (Double?) -> Unit) {
    companion object {
        fun empty() = AmountModalState(amount = null, onSetAmount = {})
    }
}

sealed class KeywordModalAction {
    object IncludeKeyword : KeywordModalAction()
    object ExcludeKeyWord : KeywordModalAction()
}

fun ExpandCollapseHandler.expand() = this.setExpanded(true)
fun ExpandCollapseHandler.collapse() = this.setExpanded(false)

fun emptyTransactionList() = TransactionsList(null, null, emptyList())

fun ReportFilter.hasEmptyContents() =
    this.trnTypes.isEmpty() && period == null &&
            accounts.isEmpty() && categories.isEmpty() &&
            minAmount == null && maxAmount == null &&
            includeKeywords.isEmpty() && excludeKeywords.isEmpty()

@Composable
fun reportsEmptyState() = EmptyState(
    title = stringResource(com.ivy.base.R.string.no_filter),
    description = stringResource(com.ivy.base.R.string.invalid_filter_warning)
)

