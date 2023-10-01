package com.ivy.data.model

import androidx.compose.ui.graphics.Color
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.IconId
import com.ivy.data.model.primitive.NotBlankTrimmedString
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