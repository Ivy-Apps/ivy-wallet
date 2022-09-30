package com.ivy.design.l2_components.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l1_buildingBlocks.InputField
import com.ivy.design.l2_components.input.InputFieldType.Multiline
import com.ivy.design.util.ComponentPreview

@Composable
fun IvyInputField(
    type: InputFieldType,
    value: TextFieldValue,
    placeholder: String,
    modifier: Modifier = Modifier,
    onValueChange: (TextFieldValue) -> Unit,
) {
    InputField(
        modifier = modifier,
        value = value,
        placeholder = placeholder,
        singleLine = when (type) {
            is Multiline -> false
            InputFieldType.SingleLine -> true
        },
        maxLines = when (type) {
            is Multiline -> type.maxLines
            InputFieldType.SingleLine -> Int.MAX_VALUE
        },
        onValueChange = onValueChange
    )
}


// region Previews
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        IvyInputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            type = InputFieldType.SingleLine,
            value = TextFieldValue("Input"),
            placeholder = "Placeholder",
            onValueChange = {}
        )
    }
}
// endregion