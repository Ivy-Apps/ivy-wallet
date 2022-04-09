package com.ivy.wallet.io.network.request.category

import com.ivy.wallet.domain.data.entity.Category

data class UpdateWalletCategoryRequest(
    val category: Category? = null
)