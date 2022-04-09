package com.ivy.wallet.io.network.request.category

import com.ivy.wallet.model.entity.Category

data class UpdateWalletCategoryRequest(
    val category: Category? = null
)