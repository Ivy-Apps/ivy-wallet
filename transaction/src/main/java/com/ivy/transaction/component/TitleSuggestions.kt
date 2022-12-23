package com.ivy.transaction.component

import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.util.ComponentPreview

@Composable
internal fun TitleSuggestions(
    visible: Boolean,
    suggestions: List<String>,
    modifier: Modifier = Modifier,
    onSuggestionClick: (String) -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible && suggestions.isNotEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, UI.colors.primary, UI.shapes.rounded)
                .padding(vertical = 4.dp),
        ) {
            suggestions.forEachIndexed { index, suggestion ->
                key(index.toString() + suggestion) {
                    Suggestion(suggestion = suggestion) {
                        onSuggestionClick(suggestion)
                    }
                }
            }
        }
    }
}

@Composable
private fun Suggestion(
    suggestion: String,
    onClick: () -> Unit,
) {
    B2(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        text = suggestion,
        fontWeight = FontWeight.SemiBold,
    )
}


@Preview
@Composable
private fun TitleSuggestionsPreview() {
    ComponentPreview {
        TitleSuggestions(
            visible = true,
            suggestions = listOf(
                "Suggestion 1",
                "Suggestion 2",
                "Suggestion 3"
            ),
            onSuggestionClick = {},
        )
    }
}