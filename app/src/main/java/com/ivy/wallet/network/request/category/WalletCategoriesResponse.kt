package com.ivy.wallet.network.request.category

import com.ivy.wallet.model.entity.Category


data class WalletCategoriesResponse(
    val categories: List<Category>
)