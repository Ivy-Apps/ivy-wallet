package com.ivy.core.ui.icon.picker

import com.ivy.data.ItemIconId

sealed interface PickerItemUnverified {
    data class Icon(
        val iconId: ItemIconId,
        val keywords: List<String> = emptyList(),
    ) : PickerItemUnverified

    data class SectionDivider(val name: String) : PickerItemUnverified
}