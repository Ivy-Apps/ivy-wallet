package com.ivy.data.model

import androidx.compose.ui.graphics.Color
import com.ivy.data.model.common.Reorderable
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.sync.Syncable
import com.ivy.data.model.sync.UniqueId
import java.time.Instant
import java.util.UUID

@JvmInline
value class AccountId(override val value: UUID) : UniqueId

data class Account(
    override val id: AccountId,
    val name: NotBlankTrimmedString,
    val asset: AssetCode,
    val color: Color,
    val icon: IconAsset?,
    val includeInBalance: Boolean,
    override val orderNum: Double,
    override val lastUpdated: Instant,
    override val removed: Boolean,
) : Syncable, Reorderable