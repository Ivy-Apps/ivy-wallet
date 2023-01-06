package com.ivy.design.l3_ivyComponents

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.design.R
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview

@Composable
fun ReorderButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IvyButton(
        modifier = modifier,
        size = ButtonSize.Small,
        visibility = Visibility.Medium,
        feeling = Feeling.Positive,
        text = null,
        icon = R.drawable.round_reorder_24,
        onClick = onClick,
    )
}


// region Preview
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        ReorderButton {}
    }
}
// endregion