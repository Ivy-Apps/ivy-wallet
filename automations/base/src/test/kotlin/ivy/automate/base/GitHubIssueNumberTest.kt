package ivy.automate.base

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import ivy.automate.base.github.GitHubIssueNumber

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
        GitHubIssueNumber.from("2763").shouldBeRight()
    }
})