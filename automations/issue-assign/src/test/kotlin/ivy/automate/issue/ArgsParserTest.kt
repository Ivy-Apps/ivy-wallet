package ivy.automate.issue

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ivy.automate.base.github.model.GitHubIssueNumber
import ivy.automate.base.github.model.GitHubPAT
import ivy.automate.issue.Constants.ARG_GITHUB_PAT
import ivy.automate.issue.Constants.ARG_ISSUE_NUMBER

class ArgsParserTest : FreeSpec({
    "invalid" - {
        "no args" {
            // given
            val args = emptyList<String>()

            // when
            val res = parseArgs(args)

            // then
            res.shouldBeLeft()
        }

        "random args" {
            // given
            val args = listOf("hello", "world")

            // when
            val res = parseArgs(args)

            // then
            res.shouldBeLeft()
        }

        "missing issueId" {
            // given
            val args = listOf("$ARG_GITHUB_PAT=abc")

            // when
            val res = parseArgs(args)

            // then
            res.shouldBeLeft()
        }

        "missing gitHubPAT" {
            // given
            val args = listOf("$ARG_ISSUE_NUMBER=1234")

            // when
            val res = parseArgs(args)

            // then
            res.shouldBeLeft()
        }

        "blank arguments" {
            // given
            val args = listOf("$ARG_ISSUE_NUMBER=", "$ARG_GITHUB_PAT=")

            // when
            val res = parseArgs(args)

            // then
            res.shouldBeLeft()
        }
    }

    "valid args" {
        // given
        val args = listOf("$ARG_ISSUE_NUMBER=123", "$ARG_GITHUB_PAT=abc")

        // when
        val res = parseArgs(args)

        // then
        res.shouldBeRight() shouldBe Args(
            pat = GitHubPAT("abc"),
            issueNumber = GitHubIssueNumber("123")
        )
    }
})