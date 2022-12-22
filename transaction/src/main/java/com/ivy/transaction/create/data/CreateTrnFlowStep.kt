package com.ivy.transaction.create.data

enum class CreateTrnFlowStep(private val key: String) {
    Title("title"),
    Description("description"),
    Amount("amount"),
    Account("account"),
    Category("category"),
    Time("time"),
    Type("type"),
}
