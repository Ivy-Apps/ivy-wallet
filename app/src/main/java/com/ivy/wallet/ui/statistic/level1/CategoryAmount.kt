package com.ivy.wallet.ui.statistic.level1

import com.ivy.wallet.domain.data.entity.Category

data class CategoryAmount(
    val category: Category?,
    val amount: Double
)