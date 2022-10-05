package com.ivy.design.l1_buildingBlocks

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.util.ComponentPreview

@Composable
fun InputField(
    initialValue: String,
    placeholder: String,
    singleLine: Boolean,
    maxLines: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    shape: Shape = UI.shapes.rounded,
    textStyle: TextStyle = UI.typo.b2.style(fontWeight = FontWeight.Bold),
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardCapitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: KeyboardActionScope.(ImeAction) -> Unit = {
        defaultKeyboardAction(it)
    },
    onValueChange: (String) -> Unit,
) {
    var textField by remember {
        // move the cursor at the end of the text
        val selection = TextRange(initialValue.length)
        mutableStateOf(TextFieldValue(initialValue, selection))
    }
    OutlinedTextField(
        modifier = modifier,
        value = textField,
        onValueChange = {
            textField = it
            onValueChange(it.text)
        },
        shape = shape,
        textStyle = textStyle,
        placeholder = {
            Text(
                text = placeholder,
                typo = textStyle,
                color = UI.colors.neutral
            )
        },
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        singleLine = singleLine,
        maxLines = maxLines,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = UI.colorsInverted.pure,
            cursorColor = UI.colorsInverted.pure,
            backgroundColor = UI.colors.pure,
            focusedBorderColor = UI.colors.primary,
            focusedLabelColor = UI.colors.primary,
            disabledBorderColor = UI.colors.neutral,
            disabledLabelColor = UI.colors.neutral,
            errorBorderColor = UI.colors.red,
            errorLabelColor = UI.colors.red,
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = keyboardCapitalization,
            autoCorrect = true,
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        visualTransformation = visualTransformation,
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction(ImeAction.Done)
            },
            onGo = {
                onImeAction(ImeAction.Go)
            },
            onNext = {
                onImeAction(ImeAction.Next)
            },
            onPrevious = {
                onImeAction(ImeAction.Previous)
            },
            onSearch = {
                onImeAction(ImeAction.Search)
            },
            onSend = {
                onImeAction(ImeAction.Send)
            },
        )
    )
}


// region Preview
@Preview
@Composable
private fun Preview_Hint() {
    ComponentPreview {
        InputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            initialValue = "",
            placeholder = "Placeholder",
            singleLine = true,
            maxLines = 1,
            onValueChange = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Text() {
    ComponentPreview {
        InputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            initialValue = "Input",
            placeholder = "Placeholder",
            singleLine = true,
            maxLines = 1,
            onValueChange = {}
        )
    }
}
// endregion