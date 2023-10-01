package com.ivy.data.model

import androidx.compose.ui.graphics.Color
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import java.util.UUID

@JvmInline
value class CategoryId(val value: UUID)

data class Category(
    val id: CategoryId,
    val name: NotBlankTrimmedString,
    val color: Color,
    val icon: IconAsset?,
    val orderNum: Double,
    val isSynced: Boolean,
    val isDeleted: Boolean,
)