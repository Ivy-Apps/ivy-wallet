package com.ivy.core.ui.color

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.design.l0_system.UI
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Choose
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe

@Composable
fun BoxScope.ColorPickerModal(
    modal: IvyModal,
    initialColor: Color?,
    onColorPicked: (Color) -> Unit,
) {
    val viewModel: ColorPickerViewModel? = hiltViewmodelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState() ?: previewState()

    var selectedColor by remember(initialColor) { mutableStateOf(initialColor) }

    Modal(
        modal = modal,
        actions = {
            Choose {
                selectedColor?.let(onColorPicked)
                modal.hide()
            }
        }
    ) {

    }
}


// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        ColorPickerModal(
            modal = modal,
            initialColor = UI.colors.primary,
            onColorPicked = {}
        )
    }
}

private fun previewState() = ColorPickerState(
    sections = emptyList() // TODO: Add Preview state
)
// endregion