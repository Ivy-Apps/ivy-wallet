package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.util.ComponentPreview
import com.ivy.wallet.utils.isNotNullOrBlank
import com.ivy.wallet.utils.thenIf


@Composable
fun IvyOutlinedTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    hint: String?,
    hintColor: Color = UI.colors.gray,
    backgroundColor: Color = UI.colors.primary,
    emptyBorderColor: Color = UI.colors.gray,
    textColor: Color = UI.colors.pureInverse,
    cursorColor: Color = UI.colors.pureInverse,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    validateInput: (TextFieldValue) -> Boolean = { it.text.isNotNullOrBlank() },
    onValueChanged: (TextFieldValue) -> Unit
) {
    val isEmpty = value.text.isBlank()

    Box(
        modifier = modifier
            .clip(UI.shapes.rFull)
            .border(
                width = 2.dp,
                color = if (isEmpty) emptyBorderColor else backgroundColor,
                shape = UI.shapes.rFull
            )
            .thenIf(validateInput(value)) {
                background(backgroundColor.copy(alpha = 0.1f), UI.shapes.rFull)
            },
        contentAlignment = Alignment.Center
    ) {
        val inputFieldFocus = FocusRequester()

        if (isEmpty && hint.isNotNullOrBlank()) {
            Text(
                modifier = Modifier
                    .clickable {
                        inputFieldFocus.requestFocus()
                    }
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                text = hint!!,
                textAlign = TextAlign.Center,
                style = UI.typo.b2.style(
                    color = hintColor,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        BasicTextField(
            modifier = Modifier
                .focusRequester(inputFieldFocus)
                .clickable {
                    inputFieldFocus.requestFocus()
                }
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp),
            value = value,
            onValueChange = onValueChanged,
            textStyle = UI.typo.b2.style(
                color = textColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            cursorBrush = SolidColor(cursorColor),
            visualTransformation = visualTransformation,
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions
        )
    }
}


@Preview
@Composable
private fun PreviewOutlineTextField() {
    ComponentPreview {
        IvyOutlinedTextField(
            modifier = Modifier.padding(horizontal = 24.dp),
            value = TextFieldValue(),
            hint = "Hint",
            onValueChanged = {})
    }
}