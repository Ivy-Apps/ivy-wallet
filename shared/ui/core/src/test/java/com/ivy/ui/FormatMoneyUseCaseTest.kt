package com.ivy.ui

import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.ivy.base.time.TimeProvider
import com.ivy.domain.features.Features
import com.ivy.ui.time.TimeFormatter
import org.junit.Test
import org.junit.runner.RunWith
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk


@RunWith(TestParameterInjector::class)
class FormatMoneyUseCaseTest {

    @Test
    fun `my test`(){
        val features = mockk<TimeFormatter>()
        1 shouldBe 1
    }
}