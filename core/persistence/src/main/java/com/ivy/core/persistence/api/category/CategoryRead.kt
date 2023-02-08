package com.ivy.core.persistence.api.category

import com.ivy.core.data.Category
import com.ivy.core.data.CategoryId
import com.ivy.core.persistence.api.ReadSyncable

interface CategoryRead : ReadSyncable<Category, CategoryId, CategoryQuery> {
}

sealed interface CategoryQuery
