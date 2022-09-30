package com.ivy.core.ui.icon.picker

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.icon.picker.data.SectionUi

@Immutable
internal data class IconPickerStateUi(
    val sections: List<SectionUi>,
    val searchQuery: String,
)