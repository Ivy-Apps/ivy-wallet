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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.ui.IvyWalletComponentPreview
import com.ivy.wallet.utils.hideKeyboard
import com.ivy.wallet.utils.isNotNullOrBlank


@Composable
fun IvyDescriptionTextField(
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    testTag: String = "desc_input",
    value: TextFieldValue,
    hint: String?,
    fontWeight: FontWeight = FontWeight.Medium,
    textColor: Color = UI.colors.pureInverse,
    hintColor: Color = UI.colors.mediumInverse,
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
                style = UI.typo.b2.style(
                    color = hintColor,
                    fontWeight = fontWeight,
                    textAlign = TextAlign.Start
                )
            )
        }

        val view = LocalView.current
        BasicTextField(
            modifier = textModifier.testTag(testTag),
            value = value,
            onValueChange = onValueChanged,
            textStyle = UI.typo.nB2.style(
                color = textColor,
                fontWeight = fontWeight,
                textAlign = TextAlign.Start
            ),
            singleLine = false,
            cursorBrush = SolidColor(UI.colors.pureInverse),
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
    IvyWalletComponentPreview {
        IvyDescriptionTextField(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .background(UI.colors.red)
                .padding(horizontal = 24.dp),
            value = TextFieldValue(),
            hint = "Hint",
            onValueChanged = {})
    }
}

@Preview
@Composable
private fun PreviewIvyTextField_longText() {
    IvyWalletComponentPreview {
        IvyDescriptionTextField(
            modifier = Modifier
                .background(UI.colors.red)
                .padding(horizontal = 24.dp),
            value = TextFieldValue("Cur habitio favere? Sunt navises promissio grandis, primus accolaes. Yes, there is chaos, it contacts with light."),
            hint = "Hint",
            onValueChanged = {})
    }
}