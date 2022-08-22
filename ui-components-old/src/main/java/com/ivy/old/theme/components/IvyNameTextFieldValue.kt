package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.layout.*
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
import com.ivy.wallet.utils.hideKeyboard
import com.ivy.wallet.utils.isNotNullOrBlank


@Composable
fun IvyNameTextField(
    modifier: Modifier = Modifier,
    underlineModifier: Modifier = Modifier,
    value: TextFieldValue,
    textColor: Color = UI.colors.pureInverse,
    hint: String?,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        autoCorrect = true,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done,
        capitalization = KeyboardCapitalization.Sentences
    ),
    keyboardActions: KeyboardActions? = null,
    onValueChanged: (TextFieldValue) -> Unit
) {
    Column {
        val isEmpty = value.text.isBlank()

        Box(
            modifier = modifier,
            contentAlignment = Alignment.CenterStart
        ) {
            if (isEmpty && hint.isNotNullOrBlank()) {
                Text(
                    modifier = Modifier,
                    text = hint!!,
                    style = UI.typo.b2.style(
                        color = UI.colors.gray,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start
                    ),
                )
            }

            val view = LocalView.current
            BasicTextField(
                modifier = Modifier
                    .testTag("base_input"),
                value = value,
                onValueChange = onValueChanged,
                textStyle = UI.typo.b1.style(
                    color = textColor,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Start
                ),
                singleLine = false,
                cursorBrush = SolidColor(UI.colors.pureInverse),
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions ?: KeyboardActions(
                    onDone = {
                        hideKeyboard(view)
                    }
                )
            )
        }

        Spacer(Modifier.height(8.dp))

        IvyDividerLineRounded(
            modifier = underlineModifier
        )
    }
}


@Preview
@Composable
private fun PreviewIvyNameTextField() {
    com.ivy.core.ui.temp.ComponentPreview {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            IvyNameTextField(
                modifier = Modifier.padding(horizontal = 32.dp),
                underlineModifier = Modifier.padding(horizontal = 24.dp),
                value = TextFieldValue("Title"),
                hint = "Title",
                onValueChanged = {}
            )
        }

    }
}