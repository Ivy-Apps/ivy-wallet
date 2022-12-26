package com.ivy.transaction.action

import com.ivy.common.toUUID
import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.transaction.TrnQuery
import com.ivy.core.domain.action.transaction.TrnsFlow
import com.ivy.core.ui.data.CategoryUi
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
    )

    override fun Input.createFlow(): Flow<List<String>> =
        trnsFlow(TrnQuery.ByCategoryId(categoryUi?.id?.toUUID()))
            .map { categoryTrns ->
                suggestTitle(
                    categoryTrns = categoryTrns,
                    title = title,
                )
            }
}