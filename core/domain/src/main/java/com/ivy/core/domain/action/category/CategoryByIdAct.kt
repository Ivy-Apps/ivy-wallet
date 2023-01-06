package com.ivy.core.domain.action.category

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.category.CategoryDao
import com.ivy.data.category.Category
import javax.inject.Inject

class CategoryByIdAct @Inject constructor(
    private val categoryDao: CategoryDao
) : Action<String, Category?>() {
    override suspend fun String.willDo(): Category? =
        categoryDao.findById(this)?.let(::toDomain)
}