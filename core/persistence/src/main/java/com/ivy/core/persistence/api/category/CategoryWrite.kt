package com.ivy.core.persistence.api.category

import com.ivy.core.data.Category
import com.ivy.core.data.CategoryId
import com.ivy.core.persistence.api.Write

interface CategoryWrite : Write<Category, CategoryId>