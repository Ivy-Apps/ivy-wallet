package com.ivy.wallet.ui.edit.core

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.Constants
import com.ivy.base.IvyWalletComponentPreview
import com.ivy.data.transaction.TransactionType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.base.R
import com.ivy.wallet.ui.theme.components.IvyTitleTextField
import com.ivy.wallet.utils.keyboardVisibleState
import com.ivy.wallet.utils.selectEndTextFieldValue
import kotlinx.coroutines.launch
import java.util.*


@Composable
fun ColumnScope.Title(
    type: TransactionType,
    titleFocus: FocusRequester,
    initialTransactionId: UUID?,

    titleTextFieldValue: TextFieldValue,
    setTitleTextFieldValue: (TextFieldValue) -> Unit,
    suggestions: Set<String>,
    scrollState: ScrollState? = null,

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
            TransactionType.INCOME -> stringResource(R.string.income_title)
            TransactionType.EXPENSE -> stringResource(R.string.expense_title)
            TransactionType.TRANSFER -> stringResource(R.string.transfer_title)
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

    val coroutineScope = rememberCoroutineScope()
    Suggestions(
        suggestions = suggestions,
    ) { suggestion ->
        setTitleTextFieldValue(selectEndTextFieldValue(suggestion))
        onTitleChanged(suggestion)

        coroutineScope.launch {
            //scroll to top for better UX
            scrollState?.animateScrollTo(0)
        }
    }
}

@Composable
private fun Suggestions(
    suggestions: Set<String>,
    onClick: (String) -> Unit
) {
    val keyboardVisible by keyboardVisibleState()
    if (keyboardVisible) {
        if (suggestions.isNotEmpty()) {
            for (suggestion in suggestions.take(Constants.SUGGESTIONS_LIMIT)) {
                Suggestion(suggestion = suggestion) {
                    onClick(suggestion)
                }
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
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(horizontal = 24.dp)
            .padding(vertical = 12.dp),
        text = suggestion,
        style = UI.typo.b2.style(
            fontWeight = FontWeight.Medium
        )
    )
}

@Preview
@Composable
private fun PreviewTitleWithSuggestions() {
    IvyWalletComponentPreview {
        Column {
            Title(
                type = TransactionType.EXPENSE,
                titleFocus = FocusRequester(),
                initialTransactionId = null,
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