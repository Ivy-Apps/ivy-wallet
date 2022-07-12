package com.ivy.base

import com.ivy.data.Category

fun Category.isSubCategory(): Boolean = parentCategoryId != null