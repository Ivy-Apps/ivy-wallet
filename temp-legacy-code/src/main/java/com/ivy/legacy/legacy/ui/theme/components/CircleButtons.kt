package com.ivy.wallet.ui.theme.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.legacy.IvyWalletComponentPreview
import com.ivy.resources.R
import com.ivy.wallet.ui.theme.Gradient

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun CloseButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    CircleButton(
        modifier = modifier,
        icon = R.drawable.ic_dismiss,
        contentDescription = "close",
        onClick = onClick,
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun CircleButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    contentDescription: String = "icon",
    backgroundColor: Color = UI.colors.pure,
    borderColor: Color = UI.colors.medium,
    tint: Color? = UI.colors.pureInverse,
    onClick: () -> Unit,
) {
    Icon(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor, CircleShape)
            .border(2.dp, borderColor, CircleShape)
            .clickable(onClick = onClick) // enlarge click area
            .padding(6.dp),
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        tint = tint ?: Color.Unspecified,
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun CircleButtonFilled(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    contentDescription: String = "icon",
    backgroundColor: Color = UI.colors.medium,
    tint: Color? = UI.colors.pureInverse,
    clickAreaPadding: Dp = 8.dp,
    onClick: () -> Unit,
) {
    Icon(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor, CircleShape)
            .clickable(onClick = onClick) // enlarge click area
            .padding(clickAreaPadding),
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        tint = tint ?: Color.Unspecified,
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun CircleButtonFilledGradient(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    contentDescription: String = "icon",
    iconPadding: Dp = 8.dp,
    backgroundGradient: Gradient = Gradient.solid(UI.colors.medium),
    tint: Color? = UI.colors.pureInverse,
    onClick: () -> Unit,
) {
    Icon(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundGradient.asHorizontalBrush(), CircleShape)
            .clickable(onClick = onClick) // enlarge click area
            .padding(iconPadding),
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        tint = tint ?: Color.Unspecified,
    )
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    CircleButton(
        modifier = modifier,
        icon = R.drawable.ic_back,
        contentDescription = "back",
        onClick = onClick,
    )
}

@Preview
@Composable
private fun PreviewCloseButton() {
    IvyWalletComponentPreview {
        CloseButton {
        }
    }
}

@Preview
@Composable
private fun PreviewBackButton() {
    IvyWalletComponentPreview {
        BackButton {
        }
    }
}

@Preview
@Composable
private fun PreviewCircleButtonFilled() {
    IvyWalletComponentPreview {
        CircleButtonFilled(
            icon = R.drawable.ic_sort_by_alpha_24,
            onClick = {},
            clickAreaPadding = 12.dp,
        )
    }
}

@Preview
@Composable
private fun PreviewCircleButtonFilledGradient() {
    IvyWalletComponentPreview {
        CircleButtonFilledGradient(
            icon = R.drawable.ic_sort_by_alpha_24,
            onClick = {},
            iconPadding = 12.dp,
        )
    }
}
