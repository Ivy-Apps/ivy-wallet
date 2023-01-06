package com.ivy.transaction.modal

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.input.InputFieldType
import com.ivy.design.l2_components.input.IvyInputField
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Positive
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.l3_ivyComponents.button.DeleteButton
import com.ivy.design.util.IvyPreview
import com.ivy.transaction.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxScope.DescriptionModal(
    modal: IvyModal,
    initialDescription: String?,
    level: Int = 1,
    onDescriptionChange: (String?) -> Unit,
) {
    var description by remember {
        mutableStateOf(initialDescription)
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    Modal(
        modal = modal,
        level = level,
        actions = {
            if (description != null) {
                DeleteButton {
                    onDescriptionChange(null)
                    modal.hide()
                }
                SpacerHor(width = 8.dp)
            }
            Positive(
                text = if (description != null)
                    stringResource(R.string.add) else stringResource(R.string.save)
            ) {
                keyboardController?.hide()
                onDescriptionChange(description)
                modal.hide()
            }
        }
    ) {
        Title(text = stringResource(R.string.description))
        SpacerVer(height = 24.dp)

        val focus = remember { FocusRequester() }
        IvyInputField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focus)
                .padding(horizontal = 16.dp),
            type = InputFieldType.Multiline(),
            initialValue = description ?: "",
            placeholder = stringResource(R.string.description_text_field_hint),
            onValueChange = {
                description = it
            }
        )
        LaunchedEffect(Unit) {
            focus.requestFocus()
        }

        SpacerVer(height = 24.dp)
    }
}


@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        DescriptionModal(
            modal = modal,
            initialDescription = "",
            onDescriptionChange = {}
        )
    }

}