package com.ivy.testing

import androidx.compose.runtime.Composable
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest

fun <T> viewModelTest(
    block: @Composable () -> T,
    validate: suspend TurbineTestContext<T>.() -> Unit
) {
    runTest {
        moleculeFlow(mode = RecompositionMode.Immediate) {
            block()
        }.test {
            validate(this)
            cancel()
        }
    }
}