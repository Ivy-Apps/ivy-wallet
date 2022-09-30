package com.ivy.core.ui.icon.picker

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.icon.picker.data.PickerItemUi

@Immutable
data class IconPickerStateUi(
    val items: List<PickerItemUi>,
    val searchQuery: String,
)