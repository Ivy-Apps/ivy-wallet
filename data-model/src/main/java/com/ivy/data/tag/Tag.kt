package com.ivy.data.tag

import androidx.annotation.ColorInt

data class Tag(
    val id: String,
    @ColorInt
    val color: Int,
    val name: String,
    val metadata: TagMetadata
)