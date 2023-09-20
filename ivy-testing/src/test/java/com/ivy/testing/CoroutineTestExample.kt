package com.ivy.testing

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout

class CoroutineTestExample : FreeSpec({
    class ResultWrapper(var x: Int)

    fun asyncOperation(): ResultWrapper {
        val wrapper = ResultWrapper(x = 0)
        CoroutineScope(Dispatchers.IO).launch {
            wrapper.x = 42
        }
        return wrapper
    }

    suspend fun blockingOperation(): Int {
        delay(100_000)
        return 42
    }

    "blocking operations with delay should be instant" {
        withTimeout(100) { // verify that it's instant
            runTest {
                blockingOperation() shouldBe 42
            }
        }
    }

    "waits async operations to finish" {
        runTest {
            val wrapper = asyncOperation()
            testScheduler.advanceUntilIdle()
            wrapper.x shouldBe 42
        }
    }

})