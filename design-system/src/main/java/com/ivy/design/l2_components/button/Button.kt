package com.ivy.design.l2_components.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.White
import com.ivy.design.l0_system.colorAs
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.data.*
import com.ivy.design.l1_buildingBlocks.hapticClickable
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.padding

@Suppress("unused")
@Composable
fun Btn.Text(
    text: String,
    modifier: Modifier = Modifier,
    background: Background = solid(
        color = UI.colors.primary,
        shape = UI.shapes.fullyRounded,
        padding = padding(
            horizontal = 24.dp,
            vertical = 12.dp
        )
    ),
    textStyle: TextStyle = UI.typo.b2.style(
        color = White,
        textAlign = TextAlign.Center
    ),
    hapticFeedback: Boolean = false,
    onClick: () -> Unit
) {
    Text(
        modifier = modifier
            .clipBackground(background)
            .hapticClickable(hapticFeedbackEnabled = hapticFeedback, onClick = onClick)
            .applyBackground(background),
        text = text,
        style = textStyle
    )
}

// region Previews
@Preview
@Composable
private fun Preview_Solid() {
    ComponentPreview {
        Btn.Text(
            text = "Okay",
            background = solid(
                color = UI.colors.primary,
                shape = UI.shapes.fullyRounded,
                padding = padding(
                    horizontal = 24.dp,
                    vertical = 12.dp
                )
            ),
            textStyle = UI.typo.b1.colorAs(White)
        ) {

        }
    }
}

@Preview
@Composable
private fun Preview_Outlined() {
    ComponentPreview {
        Btn.Text(
            text = "Continue",
            background = outlined(
                color = UI.colorsInverted.pure,
                width = 1.dp,
                shape = UI.shapes.fullyRounded,
                padding = padding(
                    horizontal = 24.dp,
                    vertical = 12.dp
                )
            ),
            textStyle = UI.typo.b1.colorAs(UI.colorsInverted.pure)
        ) {

        }
    }
}

@Preview
@Composable
private fun Preview_FillMaxWidth() {
    ComponentPreview {
        Btn.Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = "Add task"
        ) {

        }
    }
}
// endregion

