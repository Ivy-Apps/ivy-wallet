package com.ivy.wallet.io.network.request.category

import com.ivy.wallet.io.network.data.CategoryDTO


data class WalletCategoriesResponse(
    val categories: List<CategoryDTO>
)