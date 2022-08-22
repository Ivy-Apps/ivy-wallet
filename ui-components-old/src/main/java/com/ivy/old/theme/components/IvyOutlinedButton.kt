package com.ivy.wallet.ui.theme.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.utils.thenIf


@Composable
fun IvyOutlinedButton(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes iconStart: Int?,
    solidBackground: Boolean = false,
    iconTint: Color = UI.colors.pureInverse,
    borderColor: Color = UI.colors.medium,
    textColor: Color = UI.colors.pureInverse,
    padding: Dp = 12.dp,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(UI.shapes.rFull)
            .clickable(
                onClick = onClick
            )
            .border(2.dp, borderColor, UI.shapes.rFull)
            .thenIf(solidBackground) {
                background(UI.colors.pure, UI.shapes.rFull)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconStart != null) {
            Spacer(Modifier.width(12.dp))

            IvyIcon(
                icon = iconStart,
                tint = iconTint
            )

            Spacer(Modifier.width(4.dp))
        } else {
            Spacer(Modifier.width(24.dp))
        }

        Text(
            modifier = Modifier.padding(vertical = padding),
            text = text,
            style = UI.typo.b2.style(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )

        Spacer(Modifier.width(24.dp))
    }
}

@Composable
fun IvyOutlinedButtonFillMaxWidth(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes iconStart: Int?,
    solidBackground: Boolean = false,
    iconTint: Color = UI.colors.pureInverse,
    borderColor: Color = UI.colors.medium,
    textColor: Color = UI.colors.pureInverse,
    padding: Dp = 16.dp,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(UI.shapes.rFull)
            .clickable(
                onClick = onClick
            )
            .border(2.dp, borderColor, UI.shapes.rFull)
            .thenIf(solidBackground) {
                background(UI.colors.pure, UI.shapes.rFull)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconStart != null) {
            Spacer(Modifier.width(12.dp))

            IvyIcon(
                icon = iconStart,
                tint = iconTint
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            modifier = Modifier.padding(vertical = padding),
            text = text,
            style = UI.typo.b2.style(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )

        Spacer(Modifier.weight(1f))

        if (iconStart != null) {
            Spacer(Modifier.width(12.dp))

            IvyIcon(
                icon = iconStart,
                tint = Color.Transparent,
            )
        }
    }
}

@Preview
@Composable
private fun Preview_FillMaxWidth() {
    com.ivy.core.ui.temp.ComponentPreview {
        IvyOutlinedButtonFillMaxWidth(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Import backup file",
            iconStart = R.drawable.ic_export_csv,
            textColor = Green,
            iconTint = Green
        ) {

        }
    }
}