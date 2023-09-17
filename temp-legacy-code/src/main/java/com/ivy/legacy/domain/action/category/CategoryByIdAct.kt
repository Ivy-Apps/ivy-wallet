package com.ivy.wallet.domain.action.category

import com.ivy.core.data.db.read.CategoryDao
import com.ivy.core.datamodel.Category
import com.ivy.frp.action.FPAction
import java.util.UUID
import javax.inject.Inject

class CategoryByIdAct @Inject constructor(
    private val categoryDao: CategoryDao
) : FPAction<UUID, Category?>() {
    override suspend fun UUID.compose(): suspend () -> Category? = suspend {
        categoryDao.findById(this)?.toDomain()
    }
}
