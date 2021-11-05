package com.ivy.wallet.ui.theme.modal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.wallet.base.selectEndTextFieldValue
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.components.IvyDescriptionTextField
import java.util.*

@Composable
fun BoxWithConstraintsScope.RequestFeatureModal(
    id: UUID = UUID.randomUUID(),
    visible: Boolean,

    dismiss: () -> Unit,
    onSubmit: (title: String, body: String) -> Unit
) {
    var title by remember(id) {
        mutableStateOf(selectEndTextFieldValue(""))
    }
    var body by remember(id) {
        mutableStateOf(selectEndTextFieldValue(""))
    }


    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSet(
                label = "Submit",
                enabled = title.text.isNotBlank()
            ) {
                onSubmit(
                    title.text,
                    body.text
                )
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(text = "Request a feature")

        Spacer(Modifier.height(24.dp))

        ModalNameInput(
            hint = "What do you want?",
            autoFocusKeyboard = true,
            textFieldValue = title,
            setTextFieldValue = {
                title = it
            }
        )

        Spacer(Modifier.height(16.dp))

        IvyDescriptionTextField(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                autoCorrect = true,
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            ),
            keyboardActions = KeyboardActions(
                onAny = {
                    body = body.copy(
                        text = StringBuilder(body.text)
                            .insert(body.selection.end, "\n")
                            .toString(),
                        selection = TextRange(body.selection.end + 1)
                    )
                }
            ),
            hint = "Explain it with one sentence. (supports markdown)",
            hintColor = Gray,
            value = body,
        ) {
            body = it
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        RequestFeatureModal(
            visible = true,
            dismiss = {},
            onSubmit = { _, _ -> }
        )
    }
}