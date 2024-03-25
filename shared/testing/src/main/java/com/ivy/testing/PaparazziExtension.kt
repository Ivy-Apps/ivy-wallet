package com.ivy.testing

import app.cash.paparazzi.Paparazzi
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.junit.runner.Description
import org.junit.runners.model.Statement

class PaparazziExtension(
    val paparazzi: Paparazzi,
) : BeforeTestListener, AfterTestListener {

    override suspend fun beforeTest(testCase: TestCase) {
        val junit4Description = testCase.junit4Description
        paparazzi.apply(
            base = NoopJunitStatement,
            description = junit4Description,
        )
        paparazzi.prepare(junit4Description)
    }

    override suspend fun afterTest(testCase: TestCase, result: TestResult) {
        paparazzi.close()
    }
}

private object NoopJunitStatement : Statement() {
    override fun evaluate() = Unit
}

private val TestCase.junit4Description: Description
    get() = Description.createTestDescription(descriptor.parent.id.value, name.testName)