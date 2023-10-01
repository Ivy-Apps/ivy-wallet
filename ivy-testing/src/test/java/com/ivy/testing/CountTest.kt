package com.ivy.testing

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest

class CountTest : FreeSpec({
    "test count" {
        runTest {
            moleculeFlow(mode = RecompositionMode.Immediate) {
                counter()
            }.test {
                awaitItem() shouldBe 0
                awaitItem() shouldBe 42
                cancel()
            }
        }
    }
})