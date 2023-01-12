package com.ivy.core.domain.action.category

import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.category.CategoryDao
import com.ivy.data.category.Category
import javax.inject.Inject

class CategoryByIdAct @Inject constructor(
    private val categoryDao: CategoryDao,
    private val timeProvider: TimeProvider,
) : Action<String, Category?>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(categoryId: String): Category? =
        categoryDao.findById(categoryId)?.let {
            toDomain(it, timeProvider)
        }
}