package ivy.automate.base

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ivy.automate.base.github.GitHubPAT

class GitHubPATTest : FreeSpec({
    "invalid" - {
        "blank string" {
            GitHubPAT.from("").shouldBeLeft()
        }
    }

    "valid" {
        // given
        val token = " abc "

        // when
        val res = GitHubPAT.from(token)

        // then
        res.shouldBeRight().value shouldBe "abc"
    }
})