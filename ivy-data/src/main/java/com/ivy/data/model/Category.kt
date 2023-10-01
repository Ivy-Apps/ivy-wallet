package com.ivy.data.model

import androidx.compose.ui.graphics.Color
import com.ivy.data.model.primitive.IconId
import com.ivy.data.model.primitive.NotBlankTrimmedString
import java.util.UUID

data class Category(
    val id: UUID,
    val name: NotBlankTrimmedString,
    val color: Color,
    val icon: IconId?,
    val orderNum: Double,
    val isSynced: Boolean,
    val isDeleted: Boolean,
)