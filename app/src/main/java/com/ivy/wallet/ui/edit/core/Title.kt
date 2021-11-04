package com.ivy.wallet.ui.edit.core

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.ui.theme.components.IvyTitleTextField

@Composable
fun ColumnScope.Title(
    type: TransactionType,
    titleFocus: FocusRequester,

    titleTextFieldValue: TextFieldValue,
    setTitleTextFieldValue: (TextFieldValue) -> Unit,

    onTitleChanged: (String?) -> Unit,
    onNext: () -> Unit,
) {
    IvyTitleTextField(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .focusRequester(titleFocus),
        dividerModifier = Modifier
            .padding(horizontal = 24.dp),
        value = titleTextFieldValue,
        hint = when (type) {
            TransactionType.INCOME -> "Income title"
            TransactionType.EXPENSE -> "Expense title"
            TransactionType.TRANSFER -> "Transfer title"
        },
        keyboardOptions = KeyboardOptions(
            autoCorrect = true,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            capitalization = KeyboardCapitalization.Sentences
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                onNext()
            }
        )
    ) {
        setTitleTextFieldValue(it)
        onTitleChanged(it.text)
    }
}