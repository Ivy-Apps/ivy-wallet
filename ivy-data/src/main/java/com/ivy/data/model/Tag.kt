package com.ivy.data.model

import com.ivy.data.model.common.Reorderable
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.primitive.TagId
import com.ivy.data.model.sync.Syncable
import java.time.Instant

data class Tag(
    override val id: TagId,
    val name: NotBlankTrimmedString,
    val description: String?,
    val color: ColorInt,
    val icon: IconAsset?,
    override val orderNum: Double,
    val creationTimestamp: Instant,
    override val lastUpdated: Instant,
    override val removed: Boolean
) : Syncable, Reorderable