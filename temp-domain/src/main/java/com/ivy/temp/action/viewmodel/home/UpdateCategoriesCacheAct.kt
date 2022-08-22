package com.ivy.wallet.domain.action.viewmodel.home

import com.ivy.data.CategoryOld
import com.ivy.frp.action.FPAction
import javax.inject.Inject

class UpdateCategoriesCacheAct @Inject constructor(
    private val ivyWalletCtx: com.ivy.core.ui.temp.IvyWalletCtx
) : FPAction<List<CategoryOld>, List<CategoryOld>>() {
    override suspend fun List<CategoryOld>.compose(): suspend () -> List<CategoryOld> = suspend {
        val categories = this

        ivyWalletCtx.categoryMap.clear()
        ivyWalletCtx.categoryMap.putAll(categories.map { it.id to it })

        categories
    }
}