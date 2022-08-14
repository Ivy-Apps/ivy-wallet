package com.ivy.base

import com.ivy.data.CategoryOld

fun CategoryOld.isSubCategory(): Boolean = parentCategoryId != null