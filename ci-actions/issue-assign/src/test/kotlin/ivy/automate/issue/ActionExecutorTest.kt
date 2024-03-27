package ivy.automate.issue

import arrow.core.left
import arrow.core.right
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import ivy.automate.base.Constants
import ivy.automate.base.github.GitHubIssueArgs
import ivy.automate.base.github.GitHubService
import ivy.automate.base.github.model.GitHubIssueNumber
import ivy.automate.base.github.model.GitHubPAT
import ivy.automate.base.github.model.GitHubUser
import ivy.automate.base.github.model.GitHubUsername
import ivy.automate.base.github.model.NotBlankTrimmedString
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ActionExecutorTest {
    val user1 = GitHubUser(GitHubUsername("user1"))
    val user2 = GitHubUser(GitHubUsername("user2"))
    val pat = GitHubPAT("pat")
    val issueNumber = GitHubIssueNumber("1234")
    val args = GitHubIssueArgs(
        pat = pat,
        issueNumber = issueNumber,
    )

    val readContributingMsg = """
        Also, make sure to read our [Contribution Guidelines](${Constants.CONTRIBUTING_URL}).
    """.trimIndent()

    @Test
    fun `unhappy path - AlreadyTaken - comment failure`() = runTest {
        // given
        val gitHubService = mockk<GitHubService>()
        val action = Action.AlreadyTaken(
            assignee = user1,
            issueNumber = issueNumber,
            user = user2
        )
        coEvery {
            gitHubService.commentIssue(pat, issueNumber, any())
        } returns Exception("API error").left()

        // when
        val res = with(gitHubService) { action.execute(args) }

        // then
        res.shouldBeLeft()
    }

    @Test
    fun `unhappy path - NotApproved - comment failure`() = runTest {
        // given
        val gitHubService = mockk<GitHubService>()
        val action = Action.NotApproved(
            issueNumber = issueNumber,
            user = user2
        )
        coEvery {
            gitHubService.commentIssue(pat, issueNumber, any())
        } returns Exception("API error").left()

        // when
        val res = with(gitHubService) { action.execute(args) }

        // then
        res.shouldBeLeft()
    }

    @Test
    fun `unhappy path - AssignIssue - comment failure`() = runTest {
        // given
        val gitHubService = mockk<GitHubService>()
        val action = Action.AssignIssue(
            issueNumber = issueNumber,
            user = user2
        )
        coEvery {
            gitHubService.assignIssue(pat, issueNumber, any())
        } returns Unit.right()
        coEvery {
            gitHubService.commentIssue(pat, issueNumber, any())
        } returns Exception("API error").left()

        // when
        val res = with(gitHubService) { action.execute(args) }

        // then
        res.shouldBeLeft()
    }

    @Test
    fun `unhappy path - AssignIssue - assign failure`() = runTest {
        // given
        val gitHubService = mockk<GitHubService>()
        val action = Action.AssignIssue(
            issueNumber = issueNumber,
            user = user2
        )
        coEvery {
            gitHubService.assignIssue(pat, issueNumber, any())
        } returns Exception("API error").left()
        coEvery {
            gitHubService.commentIssue(pat, issueNumber, any())
        } returns Unit.right()

        // when
        val res = with(gitHubService) { action.execute(args) }

        // then
        res.shouldBeLeft()
    }

    @Test
    fun `happy path - AlreadyTaken`() = runTest {
        // given
        val gitHubService = mockk<GitHubService>()
        val action = Action.AlreadyTaken(
            assignee = user1,
            issueNumber = issueNumber,
            user = user2
        )
        coEvery {
            gitHubService.commentIssue(pat, issueNumber, any())
        } returns Unit.right()

        // when
        val res = with(gitHubService) { action.execute(args) }

        // then
        val commentText = """
                ⚠️ Hey @user2, this issue is already taken by @user1.
                **Do not start working on it!**
                Please, [pick another one](${Constants.ISSUES_URL}).
                
                $readContributingMsg
            """.trimIndent()
        res.shouldBeRight() shouldBe commentText
        coVerify(exactly = 1) {
            gitHubService.commentIssue(
                pat,
                issueNumber,
                NotBlankTrimmedString(commentText)
            )
        }
    }

    @Test
    fun `happy path - NotApproved`() = runTest {
        // given
        val gitHubService = mockk<GitHubService>()
        val action = Action.NotApproved(
            issueNumber = issueNumber,
            user = user1
        )
        coEvery {
            gitHubService.commentIssue(pat, issueNumber, any())
        } returns Unit.right()

        // when
        val res = with(gitHubService) { action.execute(args) }

        // then
        val commentText = """
                ⚠️ Hey @user1, this issue is **not approved**, yet.
                @${Constants.IVY_ADMIN} must approve it first.

                $readContributingMsg
            """.trimIndent()
        res.shouldBeRight() shouldBe commentText
        coVerify(exactly = 1) {
            gitHubService.commentIssue(
                pat,
                issueNumber,
                NotBlankTrimmedString(commentText)
            )
        }
    }

    @Test
    fun `happy path - AssignIssue`() = runTest {
        // given
        val gitHubService = mockk<GitHubService>()
        val action = Action.AssignIssue(
            issueNumber = issueNumber,
            user = user1
        )
        coEvery {
            gitHubService.assignIssue(pat, issueNumber, any())
        } returns Unit.right()
        coEvery {
            gitHubService.commentIssue(pat, issueNumber, any())
        } returns Unit.right()

        // when
        val res = with(gitHubService) { action.execute(args) }

        // then
        coVerify(exactly = 1) {
            gitHubService.assignIssue(pat, issueNumber, user1.username)
        }
        val commentText = """
                Thank you for your interest @user1! 🎉
                Issue #1234 is assigned to you. You can work on it! ✅
                
                _If you don't want to work on it now, please un-assign yourself so other contributors can take it._
                
                $readContributingMsg
            """.trimIndent()
        res.shouldBeRight() shouldBe commentText
        coVerify(exactly = 1) {
            gitHubService.commentIssue(
                pat,
                issueNumber,
                NotBlankTrimmedString(commentText)
            )
        }
    }
}
