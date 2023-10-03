package com.ivy.data.repository.impl

import com.ivy.data.repository.CategoryRepository
import com.ivy.data.repository.mapper.CategoryMapper
import com.ivy.data.source.LocalCategoryDataSource
import io.kotest.core.spec.style.FreeSpec
import io.mockk.mockk

class CategoryRepositoryImplTest : FreeSpec({
    val dataSource = mockk<LocalCategoryDataSource>()

    fun repository(): CategoryRepository = CategoryRepositoryImpl(
        mapper = CategoryMapper(),
        dataSource = dataSource
    )


})