package com.ivy.wallet.ui.theme.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R
import com.ivy.wallet.ui.IvyWalletComponentPreview
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.GradientIvy
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.utils.drawColoredShadow
import com.ivy.wallet.utils.thenIf

@Composable
fun IvyButton(
    modifier: Modifier = Modifier,
    text: String,
    backgroundGradient: Gradient = GradientIvy,
    textStyle: TextStyle = UI.typo.b2.style(
        color = White,
        fontWeight = FontWeight.Bold
    ),
    @DrawableRes iconStart: Int? = null,
    @DrawableRes iconEnd: Int? = null,
    iconTint: Color = White,
    enabled: Boolean = true,
    shadowAlpha: Float = 0.15f,
    wrapContentMode: Boolean = true,
    hasGlow: Boolean = true,
    padding: Dp = 12.dp,
    iconEdgePadding: Dp = 12.dp,
    iconTextPadding: Dp = 4.dp,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .thenIf(enabled && hasGlow) {
                drawColoredShadow(
                    color = backgroundGradient.startColor,
                    borderRadius = 0.dp,
                    shadowRadius = 16.dp,
                    alpha = shadowAlpha,
                    offsetX = 0.dp,
                    offsetY = 8.dp
                )
            }
            .clip(UI.shapes.rFull)
            .background(
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
                    iconEdgePadding = iconEdgePadding,
                    iconTextPadding = iconTextPadding
                )
            }
            iconEnd != null && !wrapContentMode -> {
                IconEnd(
                    icon = iconEnd,
                    tint = Color.Transparent,
                    iconEdgePadding = iconEdgePadding,
                    iconTextPadding = iconTextPadding
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
                    iconEdgePadding = iconEdgePadding,
                    iconTextPadding = iconTextPadding
                )
            }
            iconEnd != null -> {
                IconEnd(
                    icon = iconEnd,
                    tint = iconTint,
                    iconEdgePadding = iconEdgePadding,
                    iconTextPadding = iconTextPadding
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
    iconEdgePadding: Dp,
    iconTextPadding: Dp,
    icon: Int,
    tint: Color,
) {
    Spacer(modifier = Modifier.width(iconEdgePadding))

    Icon(
        modifier = Modifier,
        painter = painterResource(id = icon),
        contentDescription = "icon",
        tint = tint,
    )

    Spacer(modifier = Modifier.width(iconTextPadding))
}

@Composable
private fun IconEnd(
    iconEdgePadding: Dp,
    iconTextPadding: Dp,
    icon: Int,
    tint: Color,
) {
    Spacer(modifier = Modifier.width(iconTextPadding))

    Icon(
        modifier = Modifier,
        painter = painterResource(id = icon),
        contentDescription = "icon",
        tint = tint,
    )

    Spacer(modifier = Modifier.width(iconEdgePadding))
}

@Preview
@Composable
private fun PreviewIvyButtonWrapContentWithIconStart() {
    IvyWalletComponentPreview {
        IvyButton(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .wrapContentSize(),
            iconStart = R.drawable.ic_plus,
            text = "Add new",
            wrapContentMode = true
        ) {

        }
    }
}

@Preview
@Composable
private fun PreviewIvyButtonFillMaxWidthWithIconStart() {
    IvyWalletComponentPreview {
        IvyButton(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            iconStart = R.drawable.ic_plus,
            text = "Add new",
            wrapContentMode = false
        ) {

        }
    }
}

@Preview
@Composable
private fun PreviewIvyButtonWrapContentWithIconEnd() {
    IvyWalletComponentPreview {
        IvyButton(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .wrapContentSize(),
            backgroundGradient = Gradient(Ivy, Ivy),
            iconEnd = R.drawable.ic_onboarding_next_arrow,
            text = "Category 1",
            wrapContentMode = true
        ) {

        }
    }
}

@Preview
@Composable
private fun PreviewIvyButtonFillMaxWidthWithIconEnd() {
    IvyWalletComponentPreview {
        IvyButton(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            backgroundGradient = Gradient(Ivy, Ivy),
            iconEnd = R.drawable.ic_onboarding_next_arrow,
            text = "Category 1",
            wrapContentMode = false
        ) {

        }
    }
}