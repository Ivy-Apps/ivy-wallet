package com.ivy.core.domain.action.helper

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.calculate.transaction.GroupTrnsFlow
import com.ivy.core.domain.action.transaction.TrnQuery
import com.ivy.core.domain.action.transaction.TrnsFlow
import com.ivy.data.transaction.TransactionsList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import javax.inject.Inject

class TrnsListFlow @Inject constructor(
    private val trnsFlow: TrnsFlow,
    private val groupTrnsFlow: GroupTrnsFlow
) : FlowAction<TrnQuery, TransactionsList>() {

    @OptIn(FlowPreview::class)
    override fun TrnQuery.createFlow(): Flow<TransactionsList> = trnsFlow(this)
        .flatMapMerge {
            groupTrnsFlow(it)
        }
}