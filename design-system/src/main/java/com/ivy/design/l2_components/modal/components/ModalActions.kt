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
import com.ivy.design.l3_ivyComponents.button.ButtonFeeling
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.ButtonVisibility
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview

@Suppress("unused")
@Composable
fun ModalActionsScope.DynamicSave(
    item: Any?,
    onClick: () -> Unit
) {
    IvyButton(
        size = ButtonSize.Small,
        visibility = ButtonVisibility.Focused,
        feeling = ButtonFeeling.Positive,
        text = if (item != null) "Save" else "Add",
        icon = if (item != null) R.drawable.ic_round_check_24 else R.drawable.ic_round_add_24,
        onClick = onClick
    )
}

@Suppress("unused")
@Composable
fun ModalActionsScope.Set(
    onClick: () -> Unit
) {
    Positive(
        text = stringResource(R.string.set),
        icon = R.drawable.ic_round_check_24,
        visibility = ButtonVisibility.Focused,
        onClick = onClick
    )
}


@Suppress("unused")
@Composable
fun ModalActionsScope.Positive(
    text: String,
    @DrawableRes
    icon: Int? = null,
    visibility: ButtonVisibility = ButtonVisibility.Focused,
    onClick: () -> Unit
) {
    IvyButton(
        size = ButtonSize.Small,
        visibility = visibility,
        feeling = ButtonFeeling.Positive,
        text = text,
        icon = icon,
        onClick = onClick,
    )
}

@Suppress("unused")
@Composable
fun ModalActionsScope.Negative(
    text: String,
    @DrawableRes
    icon: Int? = null,
    visibility: ButtonVisibility = ButtonVisibility.Focused,
    onClick: () -> Unit
) {
    IvyButton(
        size = ButtonSize.Small,
        visibility = visibility,
        feeling = ButtonFeeling.Negative,
        text = text,
        icon = icon,
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
                Negative(text = "No", visibility = ButtonVisibility.High) {}
                SpacerHor(width = 12.dp)
                Positive(text = "Yes") {}
            }
        ) {}
    }
}
// endregion