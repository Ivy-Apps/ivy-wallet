package com.ivy.design.l3_ivyComponents.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberContrastColor
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.data.solid
import com.ivy.design.l1_buildingBlocks.data.solidWithBorder
import com.ivy.design.l1_buildingBlocks.glow
import com.ivy.design.l2_components.button.*
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.padding
import com.ivy.design.util.thenIf

@Composable
fun IvyButton(
    size: ButtonSize,
    visibility: ButtonVisibility,
    feeling: ButtonFeeling,
    text: String?,
    @DrawableRes
    icon: Int?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bgColor = when (feeling) {
        ButtonFeeling.Positive -> UI.colors.primary
        ButtonFeeling.Negative -> UI.colors.red
        ButtonFeeling.Neutral -> UI.colors.medium
    }

    val padding = padding(horizontal = 24.dp, vertical = 12.dp)

    val background = when (visibility) {
        ButtonVisibility.Focused, ButtonVisibility.High -> solid(
            shape = UI.shapes.fullyRounded,
            color = bgColor,
            padding = padding,
        )
        ButtonVisibility.Medium -> solidWithBorder(
            shape = UI.shapes.fullyRounded,
            solid = UI.colors.pure,
            borderWidth = 1.dp,
            borderColor = bgColor,
            padding = padding,
        )
        ButtonVisibility.Low -> solid(
            shape = UI.shapes.fullyRounded,
            color = UI.colors.transparent,
            padding = padding,
        )
    }

    val textColor = rememberContrastColor(
        when (visibility) {
            ButtonVisibility.Focused, ButtonVisibility.High -> bgColor
            else -> UI.colors.pure
        }
    )
    val textStyle = UI.typo.b1.style(
        color = textColor,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    )

    val sizeModifier = when (size) {
        ButtonSize.Big -> modifier.fillMaxWidth()
        ButtonSize.Small -> modifier
    }.thenIf(visibility == ButtonVisibility.Focused) {
        this.glow(bgColor)
    }

    when {
        icon != null && text != null -> {
            // Icon + Text
            Btn.TextIcon(
                modifier = sizeModifier,
                mode = when (size) {
                    ButtonSize.Big -> Mode.FillMaxWidth
                    ButtonSize.Small -> Mode.WrapContent
                },
                text = text,
                iconLeft = icon,
                iconPadding = 12.dp,
                iconTint = textColor,
                background = background,
                textStyle = textStyle,
                onClick = onClick,
            )
        }
        icon != null && text == null -> {
            // Icon only
            Btn.Icon(
                modifier = sizeModifier,
                icon = icon,
                iconTint = textColor,
                background = background,
                onClick = onClick
            )
        }
        icon == null && text != null -> {
            // Text only
            Btn.Text(
                modifier = sizeModifier,
                text = text,
                textStyle = textStyle,
                background = background,
                onClick = onClick
            )
        }
    }
}

// region Previews
@Preview
@Composable
private fun PreviewCommon() {
    ComponentPreview {
        Column {
            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Big,
                visibility = ButtonVisibility.Focused,
                feeling = ButtonFeeling.Positive,
                text = "Add",
                icon = R.drawable.ic_vue_crypto_icon
            ) {}

            SpacerVer(height = 12.dp)

            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Small,
                visibility = ButtonVisibility.Medium,
                feeling = ButtonFeeling.Negative,
                text = "Error, okay?",
                icon = null,
            ) {}

            SpacerVer(height = 12.dp)

            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Small,
                visibility = ButtonVisibility.Focused,
                feeling = ButtonFeeling.Positive,
                text = null,
                icon = R.drawable.ic_round_add_24,
            ) {}

            SpacerVer(height = 12.dp)

            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Small,
                visibility = ButtonVisibility.Focused,
                feeling = ButtonFeeling.Neutral,
                text = "Disabled button",
                icon = null,
            ) {}

            SpacerVer(height = 12.dp)

            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Small,
                visibility = ButtonVisibility.Low,
                feeling = ButtonFeeling.Positive,
                text = "Text-only",
                icon = null
            ) {}

            SpacerVer(height = 12.dp)

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SpacerHor(width = 16.dp)

                IvyButton(
                    modifier = Modifier.weight(1f),
                    size = ButtonSize.Big,
                    visibility = ButtonVisibility.High,
                    feeling = ButtonFeeling.Positive,
                    text = "Save",
                    icon = null,
                ) {}

                SpacerHor(width = 12.dp)

                IvyButton(
                    modifier = Modifier.weight(1f),
                    size = ButtonSize.Big,
                    visibility = ButtonVisibility.Medium,
                    feeling = ButtonFeeling.Negative,
                    text = "Delete",
                    icon = null,
                ) {}

                SpacerHor(width = 16.dp)
            }
        }
    }
}
// endregion