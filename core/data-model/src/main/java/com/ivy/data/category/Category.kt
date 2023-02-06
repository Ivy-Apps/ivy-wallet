package com.ivy.data.category

import androidx.annotation.ColorInt
import com.ivy.data.ItemIconId
import com.ivy.data.Sync
import java.util.*

@Deprecated("will be removed!")
data class Category(
    val id: UUID,
    val name: String,
    val type: CategoryType,
    val parentCategoryId: UUID?,
    @ColorInt
    val color: Int,
    val icon: ItemIconId?,
    val orderNum: Double,
    val state: CategoryState,
    val sync: Sync,
)