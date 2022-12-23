package com.ivy.design.l2_components.input

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.InputField
import com.ivy.design.l2_components.input.InputFieldType.Multiline
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.button.toColor
import com.ivy.design.util.ComponentPreview

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun IvyInputField(
    type: InputFieldType,
    initialValue: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    @DrawableRes
    iconLeft: Int? = null,
    shape: Shape = UI.shapes.rounded,
    feeling: Feeling = Feeling.Positive,
    typography: InputFieldTypography = InputFieldTypography.Primary,
    keyboardCapitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    imeAction: ImeAction = if (type is Multiline) ImeAction.Default else ImeAction.Done,
    onImeAction: (KeyboardActionScope.(ImeAction) -> Unit)? = null,
    onValueChange: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    InputField(
        modifier = modifier,
        initialValue = initialValue,
        placeholder = placeholder,
        isError = isError,
        iconLeft = iconLeft,
        shape = shape,
        focusedColor = feeling.toColor(),
        textStyle = when (typography) {
            InputFieldTypography.Primary -> UI.typo.b2.style(fontWeight = FontWeight.Bold)
            InputFieldTypography.Secondary -> UI.typoSecond.b2.style(fontWeight = FontWeight.Bold)
        },
        singleLine = when (type) {
            is Multiline -> false
            InputFieldType.SingleLine -> true
        },
        maxLines = when (type) {
            is Multiline -> type.maxLines
            InputFieldType.SingleLine -> Int.MAX_VALUE
        },
        keyboardCapitalization = keyboardCapitalization,
        imeAction = imeAction,
        onImeAction = {
            onImeAction?.invoke(this, it) ?: keyboardController?.hide()
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
            initialValue = "Input",
            placeholder = "Placeholder",
            onValueChange = {}
        )
    }
}
// endregion