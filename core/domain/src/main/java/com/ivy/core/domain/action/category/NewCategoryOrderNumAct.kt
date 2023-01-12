package com.ivy.core.domain.action.category

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.category.CategoryDao
import javax.inject.Inject

class NewCategoryOrderNumAct @Inject constructor(
    private val categoryDao: CategoryDao,
) : Action<Unit, Double>() {
    override suspend fun action(input: Unit): Double =
        categoryDao.findMaxNoParentOrderNum()?.plus(1) ?: 0.0
}