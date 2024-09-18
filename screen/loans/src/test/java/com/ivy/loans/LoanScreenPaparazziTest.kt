package com.ivy.loans

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.ivy.loans.loan.LoanScreenNonTabularModeUiTest
import com.ivy.loans.loan.LoanScreenTabularModeUiTest
import com.ivy.ui.testing.PaparazziScreenshotTest
import com.ivy.ui.testing.PaparazziTheme
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class LoanScreenPaparazziTest(
    @TestParameter
    private val theme: PaparazziTheme,
) : PaparazziScreenshotTest() {
    @Test
    fun `snapshot loanScreen tabular composable`() {
        snapshot(theme) {
            LoanScreenTabularModeUiTest(theme == PaparazziTheme.Dark)
        }
    }

    @Test
    fun `snapshot loanScreen non tabular composable`() {
        snapshot(theme) {
            LoanScreenNonTabularModeUiTest(theme == PaparazziTheme.Dark)
        }
    }
}