package com.ivy.wallet.ui.theme.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.ui.theme.Gradient


@Composable
fun IvyBorderButton(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = UI.typo.b2.style(
        color = UI.colors.pureInverse,
        fontWeight = FontWeight.Bold
    ),
    backgroundGradient: Gradient = Gradient.solid(UI.colors.mediumInverse),
    @DrawableRes iconStart: Int? = null,
    @DrawableRes iconEnd: Int? = null,
    iconTint: Color = UI.colors.pureInverse,
    enabled: Boolean = true,
    wrapContentMode: Boolean = true,

    padding: Dp = 12.dp,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(UI.shapes.rFull)
            .border(
                width = 2.dp,
                brush = if (enabled)
                    backgroundGradient.asHorizontalBrush() else SolidColor(UI.colors.gray),
                shape = UI.shapes.rFull
            )
            .clickable(onClick = onClick, enabled = enabled),
        verticalAlignment = Alignment.CenterVertically
    ) {

        when {
            iconStart != null -> {
                IconStart(
                    icon = iconStart,
                    tint = iconTint,
                )
            }
            iconEnd != null && !wrapContentMode -> {
                IconEnd(
                    icon = iconEnd,
                    tint = Color.Transparent
                )
            }
            else -> {
                Spacer(modifier = Modifier.width(24.dp))
            }
        }

        if (!wrapContentMode) {
            Spacer(modifier = Modifier.weight(1f))
        }

        Text(
            modifier = Modifier.padding(
                vertical = padding,
            ),
            text = text,
            style = textStyle
        )

        if (!wrapContentMode) {
            Spacer(modifier = Modifier.weight(1f))
        }

        when {
            iconStart != null && !wrapContentMode -> {
                IconStart(
                    icon = iconStart,
                    tint = Color.Transparent,
                )
            }
            iconEnd != null -> {
                IconEnd(
                    icon = iconEnd,
                    tint = iconTint,
                )
            }
            else -> {
                Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}

@Composable
private fun IconStart(
    icon: Int,
    tint: Color,
) {
    Spacer(modifier = Modifier.width(12.dp))

    Icon(
        modifier = Modifier,
        painter = painterResource(id = icon),
        contentDescription = "icon",
        tint = tint,
    )

    Spacer(modifier = Modifier.width(4.dp))
}

@Composable
private fun IconEnd(
    icon: Int,
    tint: Color,
) {
    Spacer(modifier = Modifier.width(4.dp))

    Icon(
        modifier = Modifier,
        painter = painterResource(id = icon),
        contentDescription = "icon",
        tint = tint,
    )

    Spacer(modifier = Modifier.width(12.dp))
}

@Preview
@Composable
private fun PreviewIvyBorderButton() {
    com.ivy.core.ui.temp.ComponentPreview {
        IvyBorderButton(
            text = "New label",
            iconStart = R.drawable.ic_label_hashtag
        ) {

        }
    }
}