package com.ivy.transaction.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l2_components.input.InputFieldType
import com.ivy.design.l2_components.input.IvyInputField
import com.ivy.design.util.ComponentPreview

@Composable
internal fun TitleInput(
    title: String?,
    focus: FocusRequester,
    modifier: Modifier = Modifier,
    onTitleChange: (String) -> Unit,
    onCta: () -> Unit,
) {
    IvyInputField(
        modifier = modifier
            .focusRequester(focus)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        type = InputFieldType.SingleLine,
        initialValue = title ?: "",
        placeholder = "Title",
        imeAction = ImeAction.Done,
        onImeAction = { onCta() },
        onValueChange = onTitleChange,
    )
}


@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        TitleInput(
            title = "Title",
            focus = FocusRequester(),
            onTitleChange = {},
            onCta = {},
        )
    }
}