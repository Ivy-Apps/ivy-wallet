package ivy.automate.base.github

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import ivy.automate.base.Constants.ARG_GITHUB_PAT
import ivy.automate.base.Constants.ARG_ISSUE_NUMBER
import ivy.automate.base.github.model.GitHubIssueNumber
import ivy.automate.base.github.model.GitHubPAT
import org.junit.Test

class GitHubIssueArgsTest {
    @Test
    fun `invalid - no args`() {
        // given
        val args = emptyList<String>()

        // when
        val res = parseArgs(args)

        // then
        res.shouldBeLeft()
    }

    @Test
    fun `invalid - random args`() {
        // given
        val args = listOf("hello", "world")

        // when
        val res = parseArgs(args)

        // then
        res.shouldBeLeft()
    }

    @Test
    fun `invalid - missing issueId`() {
        // given
        val args = listOf("$ARG_GITHUB_PAT=abc")

        // when
        val res = parseArgs(args)

        // then
        res.shouldBeLeft()
    }

    @Test
    fun `invalid - missing gitHubPAT`() {
        // given
        val args = listOf("$ARG_ISSUE_NUMBER=1234")

        // when
        val res = parseArgs(args)

        // then
        res.shouldBeLeft()
    }

    @Test
    fun `invalid - blank arguments`() {
        // given
        val args = listOf("$ARG_ISSUE_NUMBER=", "$ARG_GITHUB_PAT=")

        // when
        val res = parseArgs(args)

        // then
        res.shouldBeLeft()
    }

    @Test
    fun `valid args`() {
        // given
        val args = listOf("$ARG_ISSUE_NUMBER=123", "$ARG_GITHUB_PAT=abc")

        // when
        val res = parseArgs(args)

        // then
        res.shouldBeRight() shouldBe GitHubIssueArgs(
            pat = GitHubPAT("abc"),
            issueNumber = GitHubIssueNumber("123")
        )
    }
}
