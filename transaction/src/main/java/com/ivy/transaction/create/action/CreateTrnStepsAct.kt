package com.ivy.transaction.create.action

import com.ivy.core.domain.action.Action
import com.ivy.transaction.create.data.CreateTrnFlow
import com.ivy.transaction.create.data.CreateTrnStep
import javax.inject.Inject

class CreateTrnStepsAct @Inject constructor(
) : Action<Unit, CreateTrnFlow>() {

    override suspend fun action(input: Unit): CreateTrnFlow = CreateTrnFlow(
        first = CreateTrnStep.Amount,
        steps = mapOf(
            CreateTrnStep.Amount to CreateTrnStep.Category,
            CreateTrnStep.Category to CreateTrnStep.Title,
        )
    )

}