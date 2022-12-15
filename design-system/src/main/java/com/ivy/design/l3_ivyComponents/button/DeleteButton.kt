package com.ivy.design.l3_ivyComponents.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.design.R
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.util.ComponentPreview

@Composable
fun DeleteButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IvyButton(
        modifier = modifier,
        size = ButtonSize.Small,
        visibility = Visibility.High,
        feeling = Feeling.Negative,
        text = null,
        icon = R.drawable.outline_delete_24,
        onClick = onClick
    )
}


@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        DeleteButton(onClick = {})
    }
}