package com.ivy.transaction.create.action

import com.ivy.core.domain.action.Action
import com.ivy.transaction.create.data.CreateTrnFlow
import com.ivy.transaction.create.data.CreateTrnFlowStep
import javax.inject.Inject

class CreateTrnFlowAct @Inject constructor(

) : Action<Unit, CreateTrnFlow>() {
    override suspend fun Unit.willDo(): CreateTrnFlow = CreateTrnFlow(
        first = CreateTrnFlowStep.Amount,
        steps = mapOf(
            CreateTrnFlowStep.Amount to CreateTrnFlowStep.Category,
            CreateTrnFlowStep.Category to CreateTrnFlowStep.Title,
        )
    )

}