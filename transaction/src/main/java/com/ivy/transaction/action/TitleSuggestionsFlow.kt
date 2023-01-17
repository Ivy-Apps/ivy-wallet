package com.ivy.transaction.action

import arrow.core.nonEmptyListOf
import com.ivy.common.toUUID
import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.transaction.TrnQuery
import com.ivy.core.domain.action.transaction.TrnQuery.ByCategoryId
import com.ivy.core.domain.action.transaction.TrnsFlow
import com.ivy.core.domain.action.transaction.and
import com.ivy.core.ui.data.CategoryUi
import com.ivy.data.transaction.TrnPurpose
import com.ivy.transaction.pure.suggestTitle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TitleSuggestionsFlow @Inject constructor(
    private val trnsFlow: TrnsFlow,
) : FlowAction<TitleSuggestionsFlow.Input, List<String>>() {
    data class Input(
        val title: String?,
        val categoryUi: CategoryUi?,
        val transfer: Boolean,
    )

    override fun createFlow(input: Input): Flow<List<String>> =
        trnsFlow(input.suggestionsQuery()).map { trns ->
            suggestTitle(
                transactions = trns,
                title = input.title,
            )
        }

    private fun Input.suggestionsQuery(): TrnQuery =
        if (transfer) {
            ByCategoryId(
                categoryUi?.id?.toUUID()
            ) and TrnQuery.ByPurposeIn(
                nonEmptyListOf(
                    TrnPurpose.Fee,
                    TrnPurpose.TransferFrom,
                    TrnPurpose.TransferTo,
                )
            )
        } else {
            ByCategoryId(
                categoryUi?.id?.toUUID()
            )
        }
}