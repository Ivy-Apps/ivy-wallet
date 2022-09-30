package com.ivy.core.ui.transaction.handling

import androidx.compose.runtime.*

// region Expand & Collapse Handling
@Immutable
data class ExpandCollapseHandler(
    val expanded: Boolean,
    val setExpanded: (Boolean) -> Unit,
)

@Composable
fun defaultExpandCollapseHandler(): ExpandCollapseHandler {
    var expanded by remember { mutableStateOf(false) }
    return ExpandCollapseHandler(
        expanded = expanded,
        setExpanded = { expanded = it }
    )
}