package com.ivy.core.ui.icon.picker.data

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.data.icon.ItemIcon

@Immutable
interface PickerItemUi {
    @Immutable
    data class Icon(val icon: ItemIcon) : PickerItemUi

    @Immutable
    data class Section(val name: String) : PickerItemUi
}