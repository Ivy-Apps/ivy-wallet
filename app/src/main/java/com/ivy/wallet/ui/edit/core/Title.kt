package com.ivy.wallet.ui.edit.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.wallet.base.selectEndTextFieldValue
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.ui.theme.IvyComponentPreview
import com.ivy.wallet.ui.theme.Typo
import com.ivy.wallet.ui.theme.components.IvyTitleTextField
import com.ivy.wallet.ui.theme.style
import java.util.*

private const val SUGGESTIONS_LIMIT = 10

@Composable
fun ColumnScope.Title(
    type: TransactionType,
    titleFocus: FocusRequester,
    initialTransactionId: UUID?,

    titleTextFieldValue: TextFieldValue,
    setTitleTextFieldValue: (TextFieldValue) -> Unit,
    suggestions: Set<String>,

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

    Suggestions(
        suggestions = suggestions,
        initialTransactionId = initialTransactionId,
        type = type,
    ) { suggestion ->
        setTitleTextFieldValue(selectEndTextFieldValue(suggestion))
        onTitleChanged(suggestion)
    }
}

@Composable
private fun Suggestions(
    suggestions: Set<String>,
    initialTransactionId: UUID?,
    type: TransactionType,
    onClick: (String) -> Unit
) {
    //Display title suggestions only when new transaction is being created
    if (
        initialTransactionId == null && suggestions.isNotEmpty() &&
        type != TransactionType.TRANSFER
    ) {
        for (suggestion in suggestions.take(SUGGESTIONS_LIMIT)) {
            Suggestion(suggestion = suggestion) {
                onClick(suggestion)
            }
        }
    }
}

@Composable
private fun Suggestion(
    suggestion: String,
    onClick: () -> Unit
) {
    Text(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(vertical = 12.dp)
            .clickable {
                onClick()
            },
        text = suggestion,
        style = Typo.body2.style(
            fontWeight = FontWeight.Medium
        )
    )
}

@Preview
@Composable
private fun PreviewTitleWithSuggestions() {
    IvyComponentPreview {
        Column {
            Title(
                type = TransactionType.EXPENSE,
                titleFocus = FocusRequester(),
                initialTransactionId = UUID.randomUUID(),
                titleTextFieldValue = selectEndTextFieldValue(""),
                setTitleTextFieldValue = {},
                suggestions = setOf(
                    "Tabu",
                    "Harem",
                    "Club 35"
                ),
                onTitleChanged = {}
            ) {

            }
        }
    }
}