package com.ivy.design.l2_components.modal.components

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.R
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.scope.ModalActionsScope
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview

@Suppress("unused")
@Composable
fun ModalActionsScope.DynamicSave(
    item: Any?,
    hapticFeedback: Boolean = false,
    onClick: () -> Unit
) {
    IvyButton(
        size = ButtonSize.Small,
        visibility = Visibility.Focused,
        feeling = Feeling.Positive,
        text = if (item != null) "Save" else "Add",
        icon = if (item != null) R.drawable.ic_round_check_24 else R.drawable.ic_round_add_24,
        hapticFeedback = hapticFeedback,
        onClick = onClick
    )
}

@Suppress("unused")
@Composable
fun ModalActionsScope.Set(
    hapticFeedback: Boolean = false,
    onClick: () -> Unit
) {
    Positive(
        text = stringResource(R.string.set),
        icon = R.drawable.ic_round_check_24,
        visibility = Visibility.Focused,
        hapticFeedback = hapticFeedback,
        onClick = onClick
    )
}

@Suppress("unused")
@Composable
fun ModalActionsScope.Choose(
    hapticFeedback: Boolean = false,
    onClick: () -> Unit
) {
    Positive(
        text = "Choose",
        icon = R.drawable.ic_round_check_24,
        visibility = Visibility.Focused,
        hapticFeedback = hapticFeedback,
        onClick = onClick
    )
}

@Suppress("unused")
@Composable
fun ModalActionsScope.Done(
    hapticFeedback: Boolean = false,
    onClick: () -> Unit
) {
    Positive(
        text = "Done",
        icon = R.drawable.ic_round_check_24,
        visibility = Visibility.Focused,
        hapticFeedback = hapticFeedback,
        onClick = onClick
    )
}


@Suppress("unused")
@Composable
fun ModalActionsScope.Positive(
    text: String?,
    @DrawableRes
    icon: Int? = null,
    visibility: Visibility = Visibility.Focused,
    feeling: Feeling = Feeling.Positive,
    hapticFeedback: Boolean = false,
    onClick: () -> Unit
) {
    IvyButton(
        size = ButtonSize.Small,
        visibility = visibility,
        feeling = feeling,
        text = text,
        icon = icon,
        hapticFeedback = hapticFeedback,
        onClick = onClick,
    )
}

@Suppress("unused")
@Composable
fun ModalActionsScope.Negative(
    text: String,
    @DrawableRes
    icon: Int? = null,
    visibility: Visibility = Visibility.Focused,
    hapticFeedback: Boolean = false,
    onClick: () -> Unit
) {
    IvyButton(
        size = ButtonSize.Small,
        visibility = visibility,
        feeling = Feeling.Negative,
        text = text,
        icon = icon,
        hapticFeedback = hapticFeedback,
        onClick = onClick,
    )
}

@Suppress("unused")
@Composable
fun ModalActionsScope.Secondary(
    text: String?,
    @DrawableRes
    icon: Int? = null,
    feeling: Feeling = Feeling.Positive,
    hapticFeedback: Boolean = false,
    onClick: () -> Unit
) {
    IvyButton(
        size = ButtonSize.Small,
        visibility = Visibility.Medium,
        feeling = feeling,
        text = text,
        icon = icon,
        hapticFeedback = hapticFeedback,
        onClick = onClick,
    )
}

// region Previews
@Preview
@Composable
private fun Preview_DynamicSave_Add() {
    IvyPreview {
        val modal = remember { IvyModal() }
        modal.show()
        Modal(
            modal = modal,
            actions = {
                DynamicSave(item = null) {}
            }
        ) {}
    }
}

@Preview
@Composable
private fun Preview_DynamicSave_Edit() {
    IvyPreview {
        val modal = remember { IvyModal() }
        modal.show()
        Modal(
            modal = modal,
            actions = {
                DynamicSave(item = "Test") {}
            }
        ) {}
    }
}

@Preview
@Composable
private fun Preview_PositiveNegative() {
    IvyPreview {
        val modal = remember { IvyModal() }
        modal.show()
        Modal(
            modal = modal,
            actions = {
                Negative(text = "No", visibility = Visibility.High) {}
                SpacerHor(width = 12.dp)
                Positive(text = "Yes") {}
            }
        ) {}
    }
}
// endregion