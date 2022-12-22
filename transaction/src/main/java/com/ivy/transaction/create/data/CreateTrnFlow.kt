package com.ivy.transaction.create.data

data class CreateTrnFlow(
    val first: CreateTrnFlowStep,
    val steps: Map<CreateTrnFlowStep, CreateTrnFlowStep>
)