package com.ivy.wallet.compose.util

import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.waitSeconds

fun <T> T.waitSeconds(composeTestRule: IvyComposeTestRule, seconds: Int): T {
    composeTestRule.waitSeconds(seconds.toLong())
    return this
}