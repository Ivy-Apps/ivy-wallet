package com.ivy.design.l3_ivyComponents.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberContrast
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.data.solid
import com.ivy.design.l1_buildingBlocks.data.solidWithBorder
import com.ivy.design.l1_buildingBlocks.glow
import com.ivy.design.l2_components.button.*
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.padding
import com.ivy.design.util.thenIf

@Composable
fun IvyButton(
    size: ButtonSize,
    visibility: Visibility,
    feeling: Feeling,
    text: String?,
    modifier: Modifier = Modifier,
    @DrawableRes
    icon: Int? = null,
    shape: Shape = UI.shapes.fullyRounded,
    typo: TextStyle = UI.typo.b2,
    fontWeight: FontWeight = FontWeight.Bold,
    hapticFeedback: Boolean = false,
    onClick: () -> Unit,
) {
    val feelingColor = feeling.toColor()

    val iconOnly = icon != null && text == null
    val padding = when {
        iconOnly -> padding(all = 12.dp)
        icon != null -> padding(
            start = 12.dp,
            end = 20.dp,
            top = 12.dp,
            bottom = 12.dp,
        )
        else -> padding(horizontal = 20.dp, vertical = 12.dp)
    }
    val overrideShape = if (iconOnly) UI.shapes.circle else shape

    val background = when (visibility) {
        Visibility.Focused, Visibility.High -> solid(
            shape = overrideShape,
            color = feelingColor,
            padding = padding,
        )
        Visibility.Medium -> solidWithBorder(
            shape = overrideShape,
            solid = UI.colors.pure,
            borderWidth = 2.dp,
            borderColor = feelingColor,
            padding = padding,
        )
        Visibility.Low -> solid(
            shape = overrideShape,
            color = UI.colors.transparent,
            padding = padding,
        )
    }

    val textColor = when (visibility) {
        Visibility.Focused, Visibility.High ->
            rememberContrast(feelingColor)
        Visibility.Medium -> UI.colorsInverted.pure
        Visibility.Low -> feelingColor
    }
    val textStyle = typo.style(
        color = textColor,
        fontWeight = fontWeight,
        textAlign = TextAlign.Center,
    )

    val sizeModifier = when (size) {
        ButtonSize.Big -> modifier.fillMaxWidth()
        ButtonSize.Small -> modifier
    }.thenIf(visibility == Visibility.Focused) {
        this.glow(feelingColor)
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
                iconPadding = 8.dp,
                iconTint = textColor,
                background = background,
                textStyle = textStyle,
                hapticFeedback = hapticFeedback,
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
                hapticFeedback = hapticFeedback,
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
                hapticFeedback = hapticFeedback,
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
                visibility = Visibility.Focused,
                feeling = Feeling.Positive,
                text = "Add",
                icon = R.drawable.ic_vue_crypto_icon
            ) {}

            SpacerVer(height = 12.dp)

            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Small,
                visibility = Visibility.Focused,
                feeling = Feeling.Positive,
                text = "Add",
                icon = R.drawable.ic_vue_crypto_icon
            ) {}

            SpacerVer(height = 12.dp)

            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Small,
                visibility = Visibility.Medium,
                feeling = Feeling.Negative,
                text = "Error, okay?",
                icon = null,
            ) {}

            SpacerVer(height = 12.dp)

            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Small,
                visibility = Visibility.Focused,
                feeling = Feeling.Positive,
                text = null,
                icon = R.drawable.ic_round_add_24,
            ) {}

            SpacerVer(height = 12.dp)

            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Small,
                visibility = Visibility.Focused,
                feeling = Feeling.Disabled,
                text = "Disabled button",
                icon = null,
            ) {}

            SpacerVer(height = 12.dp)

            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Small,
                visibility = Visibility.Low,
                feeling = Feeling.Positive,
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
                    visibility = Visibility.High,
                    feeling = Feeling.Positive,
                    text = "Save",
                    icon = null,
                ) {}

                SpacerHor(width = 12.dp)

                IvyButton(
                    modifier = Modifier.weight(1f),
                    size = ButtonSize.Big,
                    visibility = Visibility.Medium,
                    feeling = Feeling.Negative,
                    text = "Delete",
                    icon = null,
                ) {}

                SpacerHor(width = 16.dp)
            }

            SpacerVer(height = 16.dp)

            IvyButton(
                modifier = Modifier.padding(start = 16.dp),
                size = ButtonSize.Small,
                visibility = Visibility.Medium,
                feeling = Feeling.Negative,
                text = null,
                icon = R.drawable.round_archive_24,
            ) {}
        }
    }
}
// endregion

// region Utility functions
@Composable
fun Feeling.toColor(): Color = when (this) {
    Feeling.Positive -> UI.colors.primary
    Feeling.Negative -> UI.colors.red
    Feeling.Neutral -> UI.colors.neutral
    Feeling.Disabled -> UI.colors.medium
    is Feeling.Custom -> color
}
// endregion
