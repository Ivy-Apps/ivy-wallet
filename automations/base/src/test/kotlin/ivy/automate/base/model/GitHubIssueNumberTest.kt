package ivy.automate.base.model

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ivy.automate.base.github.model.GitHubIssueNumber

class GitHubIssueNumberTest : FreeSpec({
    "invalid" - {
        "blank string" {
            GitHubIssueNumber.from("").shouldBeLeft()
        }

        "not a number" {
            GitHubIssueNumber.from("123a").shouldBeLeft()
        }
    }

    "valid" {
        // given
        val id = "2763"

        // when
        val res = GitHubIssueNumber.from(id)

        // then
        res.shouldBeRight().value shouldBe "2763"
    }
})