package ivy.automate.issue

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import ivy.automate.base.github.model.GitHubIssueNumber

class ArgsParserTest : FreeSpec({
    "no args" {
        // given
        val args = emptyList<String>()

        // when
        val res = parseArgs(args)

        // then
        res.shouldBeLeft()
    }

    "invalid args" {
        // given
        val args = listOf("dfdf", "sdgdf")

        // when
        val res = parseArgs(args)

        // then
        res.shouldBeLeft()
    }

    "valid args" {
        // given
        val args = listOf("issueId=123", "sdgdf")

        // when
        val res = parseArgs(args)

        // then
        res.shouldBeRight().issueId shouldBe GitHubIssueNumber("123")
    }
})