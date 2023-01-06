package com.ivy.transaction.create.data

data class CreateTrnFlow(
    val first: CreateTrnStep,
    val steps: Map<CreateTrnStep, CreateTrnStep>
)