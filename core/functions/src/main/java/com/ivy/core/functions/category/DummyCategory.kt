package com.ivy.core.functions.category

import androidx.annotation.ColorInt
import com.ivy.core.functions.sync.dummySync
import com.ivy.data.IvyIcon
import com.ivy.data.SyncMetadata
import com.ivy.data.category.Category
import com.ivy.data.category.CategoryMetadata
import java.util.*

fun dummyCategory(
    id: UUID = UUID.randomUUID(),
    name: String = "Dummy Category",
    parentCategoryId: UUID? = null,

    @ColorInt
    color: Int = 1,
    icon: IvyIcon? = null,

    metadata: CategoryMetadata = dummyCategoryMetadata(),
): Category = Category(
    id = id,
    name = name,
    parentCategoryId = parentCategoryId,
    color = color,
    icon = icon,
    metadata = metadata
)

fun dummyCategoryMetadata(
    orderNum: Double = 0.0,
    sync: SyncMetadata = dummySync()
): CategoryMetadata = CategoryMetadata(
    orderNum = orderNum,
    sync = sync
)