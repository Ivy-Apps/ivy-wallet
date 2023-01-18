package com.ivy.home.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview
import com.ivy.home.R


@Composable
internal fun MoreMenuButton(
    onClick: () -> Unit
) {
    IvyButton(
        size = ButtonSize.Small,
        visibility = Visibility.Medium,
        feeling = Feeling.Positive,
        text = null,
        icon = R.drawable.ic_settings,
        onClick = onClick,
    )
}


// region Preview
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        MoreMenuButton {}
    }
}
// endregion