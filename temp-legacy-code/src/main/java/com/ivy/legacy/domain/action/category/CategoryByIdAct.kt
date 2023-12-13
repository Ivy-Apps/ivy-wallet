package com.ivy.wallet.domain.action.category

import com.ivy.data.db.dao.read.CategoryDao
import com.ivy.frp.action.FPAction
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.datamodel.temp.toDomain
import java.util.UUID
import javax.inject.Inject

class CategoryByIdAct @Inject constructor(
    private val categoryDao: CategoryDao
) : FPAction<UUID, Category?>() {
    override suspend fun UUID.compose(): suspend () -> Category? = suspend {
        categoryDao.findById(this)?.toDomain()
    }
}
