package com.ivy.domain.model

import androidx.compose.ui.graphics.Color
import com.ivy.domain.model.primitive.IconId
import com.ivy.domain.model.primitive.NotBlankTrimmedString
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