package com.ivy.wallet.compose.util

import androidx.compose.ui.test.*


fun <T> scrollNextUntilFound(
    container: SemanticsNodeInteraction,
    index: Int = 0,
    maxIndex: Int = 100,
    interaction: () -> T
): T {
    return try {
        scroll(
            container = container,
            toIndex = index
        )

        interaction()
    } catch (exception: Exception) {
        if (index < maxIndex) {
            scrollNextUntilFound(
                container = container,
                index = index + 1,
                maxIndex = maxIndex,
                interaction = interaction
            )
        } else throw exception
    }
}

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

/**
 * Works only for "useUnmergedTree=false"
 */
fun scroll(
    container: SemanticsNodeInteraction,
    toMatcher: SemanticsMatcher
) {
    container.performScrollToNode(toMatcher)
}