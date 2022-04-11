package com.ivy.wallet.io.network.request.category

import com.ivy.wallet.domain.data.entity.Category


data class WalletCategoriesResponse(
    val categories: List<Category>
)