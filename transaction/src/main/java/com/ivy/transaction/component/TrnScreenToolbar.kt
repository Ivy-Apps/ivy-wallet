package com.ivy.transaction.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.modal.CloseButton
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview

@Composable
internal fun TrnScreenToolbar(
    onClose: () -> Unit,
    actions: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CloseButton(onClick = onClose)
        SpacerWeight(weight = 1f)
        actions()
    }
}


@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        TrnScreenToolbar(
            onClose = {},
            actions = {
                IvyButton(
                    size = ButtonSize.Small,
                    visibility = Visibility.Medium,
                    feeling = Feeling.Positive,
                    text = "Test",
                    icon = null,
                    onClick = {}
                )
            }
        )
    }
}