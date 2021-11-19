package com.ivy.wallet.compose

import org.junit.Test


class MyComposeTest : IvyComposeTest() {

    @Test
    fun MyTest() {
        composeTestRule.printTree()
    }
}