package com.ivy.wallet.compose.util

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule

fun IvyComposeTestRule.scrollNextUntilFound(
    lazyColumnTag: String,
    index: Int = 0,
    maxIndex: Int = 10,
    interaction: () -> Unit
) {
    try {
        onNodeWithTag(lazyColumnTag)
            .performScrollToIndex(index)

        interaction()
    } catch (ignored: Exception) {
        if (index <= maxIndex) {
            scrollNextUntilFound(
                lazyColumnTag = lazyColumnTag,
                index = index + 1,
                maxIndex = maxIndex,
                interaction = interaction
            )
        }
    }
}

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