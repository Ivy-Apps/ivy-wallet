package com.ivy.data.model

import com.ivy.data.model.common.Reorderable
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.sync.Syncable
import com.ivy.data.model.sync.UniqueId
import java.time.Instant
import java.util.UUID

@JvmInline
value class CategoryId(override val value: UUID = UUID.randomUUID()) : UniqueId

data class Category(
    override val id: CategoryId = CategoryId(),
    val name: NotBlankTrimmedString,
    val color: ColorInt,
    val icon: IconAsset? = IconAsset(""),
    override val orderNum: Double = 0.0,
    override val lastUpdated: Instant = Instant.EPOCH,
    override val removed: Boolean = false,
) : Syncable, Reorderable
