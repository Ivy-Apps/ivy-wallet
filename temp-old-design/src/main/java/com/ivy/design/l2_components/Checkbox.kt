package com.ivy.design.l2_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.IvyIcon
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.utils.IvyComponentPreview
import com.ivy.design.utils.clickableNoIndication

@Composable
fun Checkbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    contentDescription: String = "checkbox",
    onCheckedChange: (checked: Boolean) -> Unit
) {
    IvyIcon(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = {
                onCheckedChange(!checked)
            })
            .padding(all = 12.dp),
        icon = if (checked) R.drawable.ic_checkbox_checked else R.drawable.ic_checkbox_unchecked,
        contentDescription = contentDescription,
        tint = if (checked) Color.Unspecified else UI.colors.gray
    )
}

@Composable
fun CheckboxWithText(
    modifier: Modifier = Modifier,
    checked: Boolean,
    text: String,
    textStyle: TextStyle = UI.typo.b2.style(
        color = UI.colors.pureInverse,
        fontWeight = FontWeight.SemiBold
    ),
    onCheckedChange: (checked: Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .clickableNoIndication {
                onCheckedChange(!checked)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        SpacerHor(width = 4.dp)

        Text(
            modifier = Modifier,
            text = text,
            style = textStyle
        )
    }
}

@Preview
@Composable
private fun PreviewIvyCheckboxWithText() {
    IvyComponentPreview {
        CheckboxWithText(
            text = "Default category",
            checked = false,
        ) {
        }
    }
}
