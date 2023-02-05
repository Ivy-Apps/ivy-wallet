package com.ivy.core.persistence.api.category

import com.ivy.core.data.CategoryId
import com.ivy.core.persistence.api.WriteSyncable
import com.ivy.data.category.Category

interface CategoryWrite : WriteSyncable<Category, CategoryId>