package com.ivy.core.domain.action

import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SharedFlowActionTest : StringSpec({

    // region Helpers
    fun newSharedFlow(initial: String, flow: Flow<String>): SharedFlowAction<String> =
        object : SharedFlowAction<String>() {
            override fun initialValue() = initial

            override fun createFlow(): Flow<String> = flow
        }
    // endregion

    // region Setup
    beforeTest {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    afterTest {
        Dispatchers.resetMain()
    }
    // endregion

    "empty flow emits initial value to multiple subscribers" {
        val sut = newSharedFlow(initial = "initial", flow = emptyFlow())

        val sub1 = sut().first()
        val sub2 = sut().first()

        sub1 shouldBe "initial"
        sub2 shouldBe "initial"
    }

    "emits initial and then latest data" {
        val innerFlow = MutableSharedFlow<String>()
        val sut = newSharedFlow(initial = "initial", flow = innerFlow)

        val sub = sut()
        launch {
            // emit value afterwards
            innerFlow.emit("emitted")
        }
        val res = sub.take(2).toList()


        res shouldBe listOf("initial", "emitted")
    }

    "computes once and emits computation data to multiple subs" {
        var executed = false
        val sut = newSharedFlow(
            initial = "initial",
            flow = flow {
                if (executed) fail("Computation executed twice")
                executed = true
                emit("result")
            }
        )

        val sub1 = sut().first()
        val sub2 = sut().first()
        val sub3 = sut().first()

        sub1 shouldBe "result"
        sub2 shouldBe "result"
        sub3 shouldBe "result"
    }

    "emits latest data to multiple subs" {
        // Arrange
        val innerFlow = MutableSharedFlow<String>()
        val sut = newSharedFlow(
            initial = "initial",
            flow = innerFlow
        )

        // Act
        val sub1 = sut()
        innerFlow.emit("1")
        val valueSub1 = sub1.first()

        innerFlow.emit("2")
        val valueSub2 = sut().first()
        innerFlow.emit("3")
        val valueSub3 = sut().first()
        val valueSub1updated = sub1.first()

        // Assert
        valueSub1 shouldBe "1"
        valueSub2 shouldBe "2"
        valueSub3 shouldBe "3"
        valueSub1updated shouldBe "3"
    }
})