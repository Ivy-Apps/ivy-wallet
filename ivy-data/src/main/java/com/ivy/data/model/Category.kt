package com.ivy.data.model

import androidx.compose.ui.graphics.Color
import com.ivy.data.model.common.Reorderable
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.sync.Syncable
import com.ivy.data.model.sync.UniqueId
import java.time.Instant
import java.util.UUID

@JvmInline
value class CategoryId(override val value: UUID) : UniqueId

data class Category(
    override val id: CategoryId,
    val name: NotBlankTrimmedString,
    val color: Color,
    val icon: IconAsset?,
    override val orderNum: Double,
    override val lastUpdated: Instant,
    override val removed: Boolean,
) : Syncable, Reorderable