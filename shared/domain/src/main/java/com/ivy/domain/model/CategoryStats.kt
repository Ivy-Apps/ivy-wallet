package com.ivy.domain.model

data class CategoryStats(
    val income: StatSummary,
    val expense: StatSummary,
) {
    companion object {
        val Zero = CategoryStats(
            income = StatSummary.Zero,
            expense = StatSummary.Zero,
        )
    }
}