package com.ivy.core.ui.color.picker.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.l0_system.color.rememberDynamicContrast
import com.ivy.design.l0_system.color.toHex
import com.ivy.design.l1_buildingBlocks.B1Second
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.input.InputFieldType
import com.ivy.design.l2_components.input.InputFieldTypography
import com.ivy.design.l2_components.input.IvyInputField
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Choose
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxScope.HexColorPickerModal(
    modal: IvyModal,
    initialColor: Color?,
    level: Int = 3,
    onColorPicked: (Color) -> Unit
) {
    val viewModel: HexColorPickerViewModel? = hiltViewModelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    if (initialColor != null) {
        LaunchedEffect(initialColor) {
            viewModel?.onEvent(HexColorPickerEvent.SetColor(initialColor))
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    Modal(
        modal = modal,
        level = level,
        actions = {
            Choose {
                keyboardController?.hide()
                state.color?.let(onColorPicked)
                modal.hide()
            }
        }
    ) {
        Title(text = "Custom Color")
        SpacerVer(height = 24.dp)
        HexInput(
            initialHex = state.hex,
            isError = state.color == null,
            feeling = state.color?.let(Feeling::Custom) ?: Feeling.Positive,
            onHexChange = {
                viewModel?.onEvent(HexColorPickerEvent.Hex(it))
            }
        )
        SpacerVer(height = 24.dp)
        PickedColor(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = state.color,
            hex = state.hex,
        )
        SpacerVer(height = 48.dp)
    }
}

// region Hex input field
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun HexInput(
    initialHex: String,
    isError: Boolean,
    feeling: Feeling,
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
        isError = isError,
        feeling = feeling,
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
    color: Color?,
    hex: String,
    modifier: Modifier = Modifier,
) {
    val dynamicContrast = color?.let { rememberDynamicContrast(color) }
    val textColor = dynamicContrast ?: UI.colors.red
    Box(
        modifier = modifier
            .size(168.dp)
            .background(color ?: UI.colors.pure, UI.shapes.rounded)
            .border(
                width = 4.dp,
                color = textColor,
                shape = UI.shapes.rounded
            ),
    ) {
        B1Second(
            modifier = Modifier.align(Alignment.Center),
            text = if (color != null) hex else "Invalid #HEX",
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }

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