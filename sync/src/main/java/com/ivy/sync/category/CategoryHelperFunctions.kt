package com.ivy.sync.category

import com.ivy.data.SyncMetadata
import com.ivy.data.category.Category
import com.ivy.wallet.io.network.data.CategoryDTO

fun Category.mark(
    isSynced: Boolean,
    isDeleted: Boolean
): Category = this.copy(
    metadata = this.metadata.copy(
        sync = SyncMetadata(
            isSynced = isSynced,
            isDeleted = isDeleted,
        )
    )
)

fun mapToDTO(cat: Category): CategoryDTO = CategoryDTO(
    id = cat.id,
    name = cat.name,
    //TODO: Handle parentCategoryId
    color = cat.color,
    icon = cat.icon,
    orderNum = cat.metadata.orderNum,
)