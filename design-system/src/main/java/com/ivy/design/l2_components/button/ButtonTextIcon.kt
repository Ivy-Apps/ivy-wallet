package com.ivy.design.l2_components.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.design.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.Transparent
import com.ivy.design.l0_system.color.White
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.IconRes
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.data.Background
import com.ivy.design.l1_buildingBlocks.data.applyBackground
import com.ivy.design.l1_buildingBlocks.data.clipBackground
import com.ivy.design.l1_buildingBlocks.data.solid
import com.ivy.design.l1_buildingBlocks.hapticClickable
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.padding

@Suppress("unused")
@Composable
fun Btn.TextIcon(
    text: String,
    modifier: Modifier = Modifier,
    mode: Mode = Mode.WrapContent,
    background: Background = solid(
        color = UI.colors.primary,
        shape = UI.shapes.fullyRounded,
        padding = padding(
            horizontal = 24.dp,
            vertical = 12.dp
        )
    ),
    textStyle: TextStyle = UI.typo.b1.style(
        color = White,
        textAlign = TextAlign.Center
    ),
    @DrawableRes iconLeft: Int? = null,
    @DrawableRes iconRight: Int? = null,
    iconTint: Color = White,
    iconPadding: Dp = 12.dp,
    hapticFeedback: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clipBackground(background)
            .hapticClickable(hapticFeedbackEnabled = hapticFeedback, onClick = onClick)
            .applyBackground(background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val icon = iconLeft ?: iconRight ?: error("If you don't add an icon, use Btn.Text()")

        if (mode == Mode.FillMaxWidth || iconLeft != null) {
            // for FillMaxWidth add an invisible icon so the text looks centered
            IconRes(
                icon = icon,
                tint = if (iconLeft != null) iconTint else Transparent
            )
            SpacerHor(width = iconPadding)
        }

        Text(
            text = text,
            style = textStyle
        )

        if (mode == Mode.FillMaxWidth || iconRight != null) {
            // for FillMaxWidth add an invisible icon so the text looks centered
            SpacerHor(width = iconPadding)
            IconRes(
                icon = icon,
                tint = if (iconRight != null) iconTint else Transparent
            )
        }
    }
}

enum class Mode {
    WrapContent, FillMaxWidth
}

@Preview
@Composable
private fun Preview_IconLeft_Wrap() {
    ComponentPreview {
        Btn.TextIcon(
            text = "Button",
            mode = Mode.WrapContent,
            iconLeft = R.drawable.ic_vue_crypto_icon
        ) {

        }
    }
}

@Preview
@Composable
private fun Preview_IconRight_Wrap() {
    ComponentPreview {
        Btn.TextIcon(
            text = "Button",
            mode = Mode.WrapContent,
            iconRight = R.drawable.ic_vue_crypto_icon
        ) {

        }
    }
}

@Preview
@Composable
private fun Preview_IconLeft_FillMax() {
    ComponentPreview {
        Btn.TextIcon(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            text = "Button",
            mode = Mode.FillMaxWidth,
            iconLeft = R.drawable.ic_vue_crypto_icon
        ) {

        }
    }
}

@Preview
@Composable
private fun Preview_IconRight_FillMax() {
    ComponentPreview {
        Btn.TextIcon(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            text = "Button",
            mode = Mode.FillMaxWidth,
            iconRight = R.drawable.ic_vue_crypto_icon
        ) {

        }
    }
}