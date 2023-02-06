package com.ivy.data.tag

import androidx.annotation.ColorInt
import com.ivy.data.Sync

@Deprecated("will be removed!")
data class Tag(
    val id: String,
    @ColorInt
    val color: Int,
    val name: String,
    val orderNum: Double,
    val state: TagState,
    val sync: Sync,
)