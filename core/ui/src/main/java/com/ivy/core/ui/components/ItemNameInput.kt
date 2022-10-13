package com.ivy.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.ui.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l2_components.input.InputFieldType
import com.ivy.design.l2_components.input.IvyInputField
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.util.ComponentPreview

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ItemNameInput(
    initialName: String,
    modifier: Modifier = Modifier,
    feeling: Feeling,
    hint: String,
    onNameChange: (String) -> Unit,
) {
    val focus = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(initialName) {
        focus.requestFocus()
        keyboardController?.show()
    }

    IvyInputField(
        modifier = modifier
            .focusRequester(focus),
        type = InputFieldType.SingleLine,
        initialValue = initialName,
        shape = UI.shapes.fullyRounded,
        feeling = feeling,
        placeholder = hint,
        onValueChange = onNameChange
    )
}


// region Preview
@Preview
@Composable
private fun Preview_Empty() {
    ComponentPreview {
        ItemNameInput(
            initialName = "",
            hint = stringResource(R.string.account_name),
            feeling = Feeling.Positive,
            onNameChange = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Filled() {
    ComponentPreview {
        ItemNameInput(
            initialName = "Cash",
            hint = stringResource(R.string.account_name),
            feeling = Feeling.Positive,
            onNameChange = {}
        )
    }
}
// endregion