package com.ivy.core.ui.color.picker.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.*
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.*
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
import kotlinx.coroutines.delay

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
    val colorController = remember(initialColor) {
        ColorPickerController()
    }
    val changeColorOnHexInput = remember(initialColor, state.hex) {
        derivedStateOf { state.color != null }
    }


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
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            userScrollEnabled = false
        ) {
            item {
                Title(text = "Custom Color")
                SpacerVer(height = 24.dp)
            }

            item {
                HexInput(
                    initialHex = state.hex,
                    isError = state.color == null,
                    feeling = state.color?.let(Feeling::Custom) ?: Feeling.Positive,
                    onHexChange = {
                        viewModel?.onEvent(HexColorPickerEvent.Hex(it))
                    }
                )
                SpacerVer(height = 24.dp)
            }

            item {
                PickedColor(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = state.color,
                    hex = state.hex,
                )
                SpacerVer(height = 24.dp)
            }

            item {
                HsvColorPicker(
                    modifier = Modifier
                        .padding(horizontal = 48.dp)
                        .aspectRatio(1f),
                    controller = colorController,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        if (colorEnvelope.fromUser)
                            viewModel?.onEvent(HexColorPickerEvent.Hex(colorEnvelope.hexCode.drop(2)))
                    }
                )
            }

            item {
                Title(text = "Brightness")
                SpacerVer(height = 12.dp)
                BrightnessSlider(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                        .height(32.dp),
                    controller = colorController
                )
            }

            item {
                SpacerVer(height = 48.dp)
            }
        }
    }

    colorController.InitialiseColorPicker(modal = modal, initialColor = initialColor)

    LaunchedEffect(key1 = changeColorOnHexInput.value) {
        if (changeColorOnHexInput.value && state.color != null && state.color != initialColor)
            colorController.selectByColor(state.color, fromUser = false, includeBrightness = true)
    }
}

@Composable
private fun ColorPickerController.InitialiseColorPicker(modal: IvyModal, initialColor: Color?) {
    LaunchedEffect(modal.visibilityState.value) {
        if (modal.visibilityState.value && initialColor != null) {
            delay(50) // fix race condition
            this@InitialiseColorPicker.selectByColor(
                initialColor,
                fromUser = false,
                includeBrightness = true
            )
        }
    }
}

// region PickedColor
@Composable
private fun HexInput(
    initialHex: String,
    isError: Boolean,
    feeling: Feeling,
    onHexChange: (String) -> Unit,
) {
    IvyInputField(
        modifier = Modifier
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
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(color ?: UI.colors.pure, UI.shapes.rounded)
            .border(
                width = 4.dp,
                color = textColor,
                shape = UI.shapes.rounded
            ),
    ) {
        B1Second(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            text = if (color != null) hex else "Invalid #HEX",
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }

}


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