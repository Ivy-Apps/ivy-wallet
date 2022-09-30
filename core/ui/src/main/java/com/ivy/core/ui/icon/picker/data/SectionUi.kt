package com.ivy.core.ui.icon.picker.data

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.data.icon.ItemIcon

@Immutable
data class SectionUi(
    val name: String,
    val iconRows: List<List<ItemIcon>>
)