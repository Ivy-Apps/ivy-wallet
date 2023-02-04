package com.ivy.core.persistence.api.category

import com.ivy.core.data.CategoryId
import com.ivy.core.persistence.api.Read
import com.ivy.data.category.Category

interface CategoryRead : Read<Category, CategoryId, CategoryRead.Query> {
    sealed interface Query
}