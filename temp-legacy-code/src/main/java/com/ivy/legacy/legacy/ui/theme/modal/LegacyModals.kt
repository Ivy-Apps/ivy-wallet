package com.ivy.legacy.legacy.ui.theme.modal

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ivy.legacy.utils.hideKeyboard
import com.ivy.legacy.utils.onScreenStart
import com.ivy.wallet.ui.theme.components.IvyNameTextField

@Composable
fun ModalNameInput(
    hint: String,
    autoFocusKeyboard: Boolean,
    textFieldValue: TextFieldValue,
    setTextFieldValue: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
) {
    val nameFocus = FocusRequester()

    onScreenStart {
        if (autoFocusKeyboard) {
            nameFocus.requestFocus()
        }
    }

    val view = LocalView.current
    IvyNameTextField(
        modifier = modifier
            .padding(start = 32.dp, end = 36.dp)
            .focusRequester(nameFocus),
        underlineModifier = Modifier.padding(start = 32.dp, end = 32.dp),
        value = textFieldValue,
        hint = hint,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text,
            autoCorrect = true
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                hideKeyboard(view)
            }
        ),
    ) { newValue ->
        setTextFieldValue(newValue)
    }
}
