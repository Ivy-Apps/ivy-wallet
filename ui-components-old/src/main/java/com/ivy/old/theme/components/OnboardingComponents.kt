package com.ivy.wallet.ui.theme.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.util.ComponentPreview
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.GradientIvy
import com.ivy.wallet.utils.drawColoredShadow
import com.ivy.wallet.utils.thenIf


@Composable
fun OnboardingButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    backgroundGradient: Gradient,
    @DrawableRes iconStart: Int? = null,
    hasNext: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .thenIf(enabled) {
                drawColoredShadow(
                    color = backgroundGradient.startColor,
                    borderRadius = 0.dp,
                    shadowRadius = 16.dp,
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
        contentAlignment = Alignment.Center
    ) {
        if (iconStart != null) {
            Image(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(vertical = 8.dp)
                    .padding(start = 24.dp),
                painter = painterResource(id = iconStart),
                contentDescription = "iconStart"
            )
        }

        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = text,
            style = UI.typo.b2.style(
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        )

        if (hasNext && enabled) {
            Image(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(vertical = 8.dp)
                    .padding(end = 24.dp),
                painter = painterResource(id = R.drawable.ic_onboarding_next_arrow),
                contentDescription = "next"
            )
        }
    }
}

@Preview
@Composable
private fun PreviewOnboardingTextField() {
    ComponentPreview {
        IvyOutlinedTextField(
            modifier = Modifier.padding(horizontal = 24.dp),
            value = TextFieldValue("iliyan.germanov971@gmail.com"),
            hint = "Enter email",
            onValueChanged = {}
        )
    }
}

@Preview
@Composable
private fun PreviewOnboardingButton() {
    ComponentPreview {
        OnboardingButton(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            text = "Login",
            backgroundGradient = GradientIvy,
            hasNext = true,
            textColor = UI.colors.pure,
            iconStart = null,
            enabled = false,
            onClick = { }
        )
    }
}