package com.ivy.debug

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.core.ui.icon.picker.IconPickerModal
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.ColumnRoot
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.button.ButtonFeeling
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.ButtonVisibility
import com.ivy.design.l3_ivyComponents.button.IvyButton

@Composable
fun BoxScope.TestScreen() {
    val viewModel: TestViewModel = hiltViewModel()
    val iconPickerModal = rememberIvyModal()
    var selectedIcon by remember { mutableStateOf<ItemIcon?>(null) }

    ColumnRoot(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SpacerWeight(weight = 1f)
        IvyButton(
            size = ButtonSize.Big,
            visibility = ButtonVisibility.Focused,
            feeling = ButtonFeeling.Positive,
            text = "Pick an icon",
            icon = null
        ) {
            iconPickerModal.show()
        }
        SpacerVer(height = 48.dp)
        selectedIcon?.let {
            ItemIcon(
                icon = it,
                size = IconSize.L
            )
        }
        SpacerWeight(weight = 1f)
    }

    IconPickerModal(
        modal = iconPickerModal,
        initialIcon = null,
        color = UI.colors.primary,
        onIconSelected = { selectedIcon = it }
    )
}