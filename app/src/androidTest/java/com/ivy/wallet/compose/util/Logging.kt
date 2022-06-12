package com.ivy.wallet.compose.util

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog

const val COMPOSE_TEST_TAG = "compose_test"

fun ComposeTestRule.printTree(useUnmergedTree: Boolean = true) {
    this.onRoot(useUnmergedTree = useUnmergedTree).printToLog(COMPOSE_TEST_TAG)
}