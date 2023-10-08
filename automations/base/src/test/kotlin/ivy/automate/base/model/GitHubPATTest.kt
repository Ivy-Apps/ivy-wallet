package ivy.automate.base.model

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ivy.automate.base.github.model.GitHubPAT

class GitHubPATTest : FreeSpec({
    "invalid" - {
        "blank string" {
            GitHubPAT.from("").shouldBeLeft()
            GitHubPAT.from(" ").shouldBeLeft()
            GitHubPAT.from("  ").shouldBeLeft()
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