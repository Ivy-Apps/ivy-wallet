package com.ivy.core.ui.icon.picker.data

import com.ivy.data.ItemIconId

internal data class Icon(
    val iconId: ItemIconId,
    val keywords: List<String> = emptyList(),
)