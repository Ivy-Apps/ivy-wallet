package com.ivy.wallet.compose.util

import androidx.compose.ui.test.*


fun <T> T.scroll(
    container: SemanticsNodeInteraction,
    toIndex: Int
): T {
    container.performScrollToIndex(index = toIndex)
    return this
}

fun <T> T.scroll(
    container: SemanticsNodeInteraction,
    toKey: String
): T {
    container.performScrollToKey(key = toKey)
    return this
}

fun <T> T.scroll(
    container: SemanticsNodeInteraction,
    toMatcher: SemanticsMatcher
): T {
    container.performScrollToNode(toMatcher)
    return this
}