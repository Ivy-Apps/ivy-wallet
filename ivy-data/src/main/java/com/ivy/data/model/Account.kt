package com.ivy.data.model

import androidx.compose.ui.graphics.Color
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import java.util.UUID

@JvmInline
value class AccountId(val value: UUID)

data class Account(
    val id: AccountId,
    val name: NotBlankTrimmedString,
    val asset: AssetCode,
    val color: Color,
    val icon: IconAsset?,
    val orderNum: Double,
    val includeInBalance: Boolean,
    val isSynced: Boolean,
    val isDeleted: Boolean,
)