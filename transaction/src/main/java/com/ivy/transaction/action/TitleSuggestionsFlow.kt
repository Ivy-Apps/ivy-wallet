package com.ivy.transaction.action

import arrow.core.nonEmptyListOf
import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.transaction.TrnQuery
import com.ivy.core.domain.action.transaction.TrnsFlow
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class TitleSuggestionsFlow @Inject constructor(
    private val trnsFlow: TrnsFlow,
) : FlowAction<TitleSuggestionsFlow.Input, List<String>>() {
    data class Input(
        val title: String?,
        val categoryUi: CategoryUi?,
        val accountUi: AccountUi,
        val trnType: TransactionType,
    )

    override fun Input.createFlow(): Flow<List<String>> {

        TODO("Not yet implemented")
    }

    private suspend fun allTransactions(): List<Transaction> =
        trnsFlow(TrnQuery.ByTypeIn(nonEmptyListOf(TransactionType.Expense, TransactionType.Income)))
            .firstOrNull() ?: emptyList()

}