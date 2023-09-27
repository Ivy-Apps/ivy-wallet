package com.ivy.wallet.domain.action.viewmodel.home

import com.ivy.legacy.datamodel.Category
import com.ivy.frp.action.FPAction
import com.ivy.legacy.IvyWalletCtx
import javax.inject.Inject

class UpdateCategoriesCacheAct @Inject constructor(
    private val ivyWalletCtx: IvyWalletCtx
) : FPAction<List<Category>, List<Category>>() {
    override suspend fun List<Category>.compose(): suspend () -> List<Category> = suspend {
        val categories = this

        ivyWalletCtx.categoryMap.clear()
        ivyWalletCtx.categoryMap.putAll(categories.map { it.id to it })

        categories
    }
}
