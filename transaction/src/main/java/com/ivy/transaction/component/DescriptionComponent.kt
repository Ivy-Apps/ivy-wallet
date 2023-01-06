package com.ivy.transaction.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview
import com.ivy.transaction.R

@Composable
internal fun DescriptionComponent(
    description: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    if (description != null) {
        Description(
            modifier = modifier,
            description = description,
            onClick = onClick,
        )
    } else {
        AddDescriptionButton(
            modifier = modifier,
            onClick = onClick
        )
    }
}

@Composable
private fun Description(
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    B2(
        modifier = modifier
            .fillMaxWidth()
            .clip(UI.shapes.rounded)
            .border(1.dp, UI.colors.neutral, UI.shapes.rounded)
            .clickable(onClick = onClick)
            .padding(all = 16.dp),
        text = description,
        fontWeight = FontWeight.Normal
    )
}

@Composable
private fun AddDescriptionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IvyButton(
        modifier = modifier,
        size = ButtonSize.Small,
        visibility = Visibility.Medium,
        feeling = Feeling.Positive,
        text = stringResource(R.string.add_description),
        icon = R.drawable.ic_round_add_24,
        onClick = onClick
    )
}


// region Preview
@Preview
@Composable
private fun Preview_NoDescription() {
    ComponentPreview {
        DescriptionComponent(
            description = null,
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Description() {
    ComponentPreview {
        DescriptionComponent(
            description = "Description\nand more\nand more\ntesting.",
            onClick = {}
        )
    }
}
// endregion