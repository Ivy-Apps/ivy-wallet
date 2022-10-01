package com.ivy.core.ui.color.picker.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.l0_system.color.toHex
import com.ivy.design.l1_buildingBlocks.Shape
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.input.InputFieldType
import com.ivy.design.l2_components.input.InputFieldTypography
import com.ivy.design.l2_components.input.IvyInputField
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Choose
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe

@Composable
fun BoxScope.HexColorPickerModal(
    modal: IvyModal,
    initialColor: Color?,
    level: Int = 3,
    onColorPicked: (Color) -> Unit
) {
    val viewModel: HexColorPickerViewModel? = hiltViewmodelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    if (initialColor != null) {
        LaunchedEffect(initialColor) {
            viewModel?.onEvent(HexColorPickerEvent.SetColor(initialColor))
        }
    }

    Modal(
        modal = modal,
        level = level,
        actions = {
            Choose {
                state.color?.let(onColorPicked)
                modal.hide()
            }
        }
    ) {
        Title(text = "Custom Color")
        SpacerVer(height = 24.dp)
        HexInput(
            initialHex = state.hex,
            onHexChange = {
                viewModel?.onEvent(HexColorPickerEvent.Hex(it))
            }
        )
        SpacerVer(height = 24.dp)
        state.color?.let {
            PickedColor(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = it
            )
        }
        SpacerVer(height = 48.dp)
    }
}

// region Hex input field
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun HexInput(
    initialHex: String,
    onHexChange: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    IvyInputField(
        modifier = Modifier
            .focusRequester(focusRequester)
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        type = InputFieldType.SingleLine,
        typography = InputFieldTypography.Secondary,
        initialValue = initialHex,
        keyboardCapitalization = KeyboardCapitalization.Characters,
        placeholder = "#RRGGBB (or #AARRGGBB)",
        onValueChange = onHexChange
    )
}
// endregion

// region PickedColor
@Composable
private fun PickedColor(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Shape(
        modifier = modifier
            .size(168.dp)
            .background(color, UI.shapes.rounded)
    )
}
// endregion


// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        HexColorPickerModal(
            modal = modal,
            initialColor = Purple,
            onColorPicked = {}
        )
    }
}

private fun previewState() = HexColorPickerState(
    hex = "#${Purple.toHex()}",
    color = Purple
)
// endregion