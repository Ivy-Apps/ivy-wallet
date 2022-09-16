package com.ivy.data.category

import androidx.annotation.ColorInt
import com.ivy.data.SyncState
import com.ivy.data.icon.IvyIcon
import java.util.*

data class Category(
    val id: UUID,
    val name: String,
    val type: CategoryType,
    val parentCategoryId: UUID?,
    @ColorInt
    val color: Int,
    val icon: IvyIcon,
    val orderNum: Double,
    val state: CategoryState,
    val sync: SyncState,
)