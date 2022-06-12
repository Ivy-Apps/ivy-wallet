package com.ivy.wallet.compose.util

import androidx.compose.ui.test.*


fun scroll(
    container: SemanticsNodeInteraction,
    toIndex: Int
) {
    container.performScrollToIndex(index = toIndex)
}

fun scroll(
    container: SemanticsNodeInteraction,
    toKey: String
) {
    container.performScrollToKey(key = toKey)
}

fun scroll(
    container: SemanticsNodeInteraction,
    toMatcher: SemanticsMatcher
) {
    container.performScrollToNode(toMatcher)
}