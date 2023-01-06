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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B2Second
import com.ivy.design.util.ComponentPreview

@Composable
internal fun TitleSuggestions(
    focused: Boolean,
    suggestions: List<String>,
    modifier: Modifier = Modifier,
    onSuggestionClick: (String) -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier
            .padding(horizontal = 16.dp),
        visible = focused && suggestions.isNotEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
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
    B2Second(
        modifier = Modifier
            .fillMaxWidth()
            .clip(UI.shapes.rounded)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        text = suggestion,
        fontWeight = FontWeight.Normal,
    )
}


@Preview
@Composable
private fun TitleSuggestionsPreview() {
    ComponentPreview {
        TitleSuggestions(
            focused = true,
            suggestions = listOf(
                "Suggestion 1",
                "Suggestion 2",
                "Suggestion 3",
            ),
            onSuggestionClick = {},
        )
    }
}