package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.util.ComponentPreview
import com.ivy.wallet.utils.clickableNoIndication


@Composable
fun IvyCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (checked: Boolean) -> Unit
) {
    Icon(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = {
                onCheckedChange(!checked)
            })
            .padding(all = 12.dp),

        painter = painterResource(
            id = if (checked) R.drawable.ic_checkbox_checked else R.drawable.ic_checkbox_unchecked
        ),
        contentDescription = null,
        tint = if (checked) Color.Unspecified else UI.colors.gray
    )
}

@Composable
fun IvyCheckboxWithText(
    modifier: Modifier = Modifier,
    text: String,
    checked: Boolean,
    onCheckedChange: (checked: Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .clickableNoIndication {
                onCheckedChange(!checked)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        IvyCheckbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = text,
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Preview
@Composable
private fun PreviewIvyCheckboxWithText() {
    ComponentPreview {
        IvyCheckboxWithText(
            text = "Default category",
            checked = false,
        ) {

        }
    }
}