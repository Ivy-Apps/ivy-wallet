package ivy.automate.issue

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ivy.automate.base.github.model.GitHubIssueNumber
import ivy.automate.base.github.model.GitHubPAT

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
            val args = listOf("dfdf", "sdgdf")

            // when
            val res = parseArgs(args)

            // then
            res.shouldBeLeft()
        }

        "missing issueId" {
            // given
            val args = listOf("issueId=123")

            // when
            val res = parseArgs(args)

            // then
            res.shouldBeLeft()
        }

        "missing gitHubPAT" {
            // given
            val args = listOf("gitHubPAT=okay")

            // when
            val res = parseArgs(args)

            // then
            res.shouldBeLeft()
        }

        "blank arguments" {
            // given
            val args = listOf("issueId=", "gitHubPAT=")

            // when
            val res = parseArgs(args)

            // then
            res.shouldBeLeft()
        }
    }

    "valid args" {
        // given
        val args = listOf("issueId=123", "gitHubPAT=abc")

        // when
        val res = parseArgs(args)

        // then
        res.shouldBeRight() shouldBe Args(
            pat = GitHubPAT("abc"),
            issueId = GitHubIssueNumber("123")
        )
    }
})