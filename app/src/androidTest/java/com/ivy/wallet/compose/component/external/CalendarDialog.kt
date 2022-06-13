package com.ivy.wallet.compose.component.external

import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.util.printTree

class CalendarDialog(
    private val composeTestRule: IvyComposeTestRule
) {
    //TODO: Find a way to pick a date from the material Calendar dialog

    fun print() {
        composeTestRule.printTree(useUnmergedTree = true)
    }
}