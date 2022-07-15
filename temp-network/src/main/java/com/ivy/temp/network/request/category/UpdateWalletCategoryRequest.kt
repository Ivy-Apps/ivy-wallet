package com.ivy.wallet.io.network.request.category

import com.ivy.wallet.io.network.data.CategoryDTO

data class UpdateWalletCategoryRequest(
    val category: CategoryDTO? = null
)