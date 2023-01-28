package com.ivy.core.data.attribute

import com.ivy.core.data.Archiveable
import com.ivy.core.data.ItemVisuals
import com.ivy.core.data.Reorderable
import java.util.*

data class Tag(
    val id: UUID,
    val visuals: ItemVisuals,
    override val orderNum: Double,
    override val archived: Boolean,
) : Reorderable, Archiveable

@JvmInline
value class TagId(val id: UUID)