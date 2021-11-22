package com.ivy.wallet.compose

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.printToString

const val COMPOSE_TEST_TAG = "compose_test"

fun ComposeTestRule.printTree() {
    this.onRoot(useUnmergedTree = false).printToLog(COMPOSE_TEST_TAG)
    println(this.onRoot(useUnmergedTree = false).printToString(100))
}