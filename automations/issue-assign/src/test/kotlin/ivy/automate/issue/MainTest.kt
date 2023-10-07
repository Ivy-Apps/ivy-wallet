package ivy.automate.issue

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ivy.automate.issue.testFun

class MainTest : FreeSpec({
    "test 1" {
        testFun(true).shouldBeRight() shouldBe 42
    }

    "test 2" {
        testFun(false).shouldBeLeft() shouldBe "err"
    }
})