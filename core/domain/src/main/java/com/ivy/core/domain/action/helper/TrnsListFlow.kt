package com.ivy.core.domain.action.helper

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.calculate.transaction.GroupTrnsFlow
import com.ivy.core.domain.action.transaction.TrnQuery
import com.ivy.core.domain.action.transaction.TrnsFlow
import com.ivy.data.transaction.TransactionsList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class TrnsListFlow @Inject constructor(
    private val trnsFlow: TrnsFlow,
    private val groupTrnsFlow: GroupTrnsFlow
) : FlowAction<TrnQuery, TransactionsList>() {

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override fun TrnQuery.createFlow(): Flow<TransactionsList> = trnsFlow(this)
        .flatMapLatest {
            groupTrnsFlow(it)
        }
}