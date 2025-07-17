package com.ivy.disclaimer

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.ivy.ui.testing.PaparazziScreenshotTest
import com.ivy.ui.testing.PaparazziTheme
import kotlinx.collections.immutable.toImmutableList
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class DisclaimerScreenPaparazziTest(
    @TestParameter
    private val theme: PaparazziTheme,
) : PaparazziScreenshotTest() {

    @Test
    fun `none checked`() {
        snapshot(theme) {
            DisclaimerScreenUi(
                viewState = DisclaimerViewState(
                    checkboxes = DisclaimerViewModel.LegalCheckboxes,
                    agreeButtonEnabled = false,
                ),
                onEvent = {}
            )
        }
    }

    @Test
    fun `all checked`() {
        snapshot(theme) {
            DisclaimerScreenUi(
                viewState = DisclaimerViewState(
                    checkboxes = DisclaimerViewModel.LegalCheckboxes.map {
                        it.copy(checked = true)
                    }.toImmutableList(),
                    agreeButtonEnabled = true,
                ),
                onEvent = {}
            )
        }
    }
}