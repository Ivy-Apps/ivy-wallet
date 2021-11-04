package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.wallet.base.hideKeyboard
import com.ivy.wallet.base.isNotNullOrBlank
import com.ivy.wallet.ui.theme.IvyComponentPreview
import com.ivy.wallet.ui.theme.IvyTheme
import com.ivy.wallet.ui.theme.Typo
import com.ivy.wallet.ui.theme.style

@Composable
fun IvyDescriptionTextField(
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    value: TextFieldValue,
    hint: String?,
    fontWeight: FontWeight = FontWeight.Medium,
    textColor: Color = IvyTheme.colors.pureInverse,
    hintColor: Color = IvyTheme.colors.mediumInverse,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions? = KeyboardOptions.Default,
    keyboardActions: KeyboardActions? = KeyboardActions.Default,
    onValueChanged: (TextFieldValue) -> Unit
) {
    val isEmpty = value.text.isBlank()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopStart
    ) {
        if (isEmpty && hint.isNotNullOrBlank()) {
            Text(
                modifier = textModifier,
                text = hint!!,
                textAlign = TextAlign.Start,
                style = Typo.body2.style(
                    color = hintColor,
                    fontWeight = fontWeight,
                    textAlign = TextAlign.Start
                )
            )
        }

        val view = LocalView.current
        BasicTextField(
            modifier = textModifier,
            value = value,
            onValueChange = onValueChanged,
            textStyle = Typo.body2.style(
                color = textColor,
                fontWeight = fontWeight,
                textAlign = TextAlign.Start
            ),
            singleLine = false,
            cursorBrush = SolidColor(IvyTheme.colors.pureInverse),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions ?: KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = keyboardActions ?: KeyboardActions(
                onDone = {
                    hideKeyboard(view)
                }
            )
        )
    }
}


@Preview
@Composable
private fun PreviewIvyTextField() {
    IvyComponentPreview {
        IvyDescriptionTextField(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .background(IvyTheme.colors.red)
                .padding(horizontal = 24.dp),
            value = TextFieldValue(),
            hint = "Hint",
            onValueChanged = {})
    }
}

@Preview
@Composable
private fun PreviewIvyTextField_longText() {
    IvyComponentPreview {
        IvyDescriptionTextField(
            modifier = Modifier
                .background(IvyTheme.colors.red)
                .padding(horizontal = 24.dp),
            value = TextFieldValue("Cur habitio favere? Sunt navises promissio grandis, primus accolaes. Yes, there is chaos, it contacts with light."),
            hint = "Hint",
            onValueChanged = {})
    }
}