package com.ivy.wallet.compose.component.home

import androidx.compose.ui.test.hasTestTag
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.clickWithRetry
import com.ivy.wallet.compose.component.account.AccountsTab

abstract class MainBottomBar<AddFabNext>(
    private val composeTestRule: IvyComposeTestRule
) {
    fun clickHomeTab(): HomeTab {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasTestTag("home")),
            maxRetries = 3
        )
        return HomeTab(composeTestRule)
    }

    fun clickAccountsTab(): AccountsTab {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasTestTag("accounts")),
            maxRetries = 3
        )
        return AccountsTab(composeTestRule)
    }

    protected fun <T> clickAddFAB(next: T): T {
        composeTestRule.clickWithRetry(
            node = composeTestRule.onNode(hasTestTag("fab_add")),
            maxRetries = 3
        )

        return next
    }

    abstract fun clickAddFAB(): AddFabNext
}