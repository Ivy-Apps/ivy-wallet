package com.ivy.accounts

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.ivy.ui.testing.PaparazziScreenshotTest
import com.ivy.ui.testing.PaparazziTheme
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class AccountsTabPaparazziTest(
    @TestParameter
    private val theme: PaparazziTheme,
) : PaparazziScreenshotTest() {
    @Test
    fun `snapshot accountTab nonCompact composable`() {
        snapshot(theme) {
            AccountsTabNonCompactUITest(theme == PaparazziTheme.Dark)
        }
    }

    @Test
    fun `snapshot accountTab compact composable`() {
        snapshot(theme) {
            AccountsTabCompactUITest(theme == PaparazziTheme.Dark)
        }
    }
}