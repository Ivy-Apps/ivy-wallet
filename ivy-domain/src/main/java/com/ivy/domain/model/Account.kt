package com.ivy.domain.model

import androidx.compose.ui.graphics.Color
import com.ivy.domain.model.primitive.AssetCode
import com.ivy.domain.model.primitive.IconId
import com.ivy.domain.model.primitive.NotBlankTrimmedString
import java.util.UUID

data class Account(
    val id: UUID,
    val name: NotBlankTrimmedString,
    val asset: AssetCode,
    val color: Color,
    val icon: IconId?,
    val orderNum: Double,
    val includeInBalance: Boolean,
    val isSynced: Boolean,
    val isDeleted: Boolean,
)