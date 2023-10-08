package ivy.automate.issue

import arrow.core.left
import arrow.core.right
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.core.spec.style.FreeSpec
import io.mockk.coEvery
import io.mockk.mockk
import ivy.automate.base.github.GitHubService
import ivy.automate.base.github.model.GitHubIssueNumber
import ivy.automate.base.github.model.GitHubPAT
import ivy.automate.base.github.model.GitHubUser
import ivy.automate.base.github.model.GitHubUsername

class ActionExecutorTest : FreeSpec({
    val user1 = GitHubUser(GitHubUsername("user1"))
    val user2 = GitHubUser(GitHubUsername("user2"))
    val pat = GitHubPAT("pat")
    val issueNumber = GitHubIssueNumber("1234")
    val args = Args(
        pat = pat,
        issueNumber = issueNumber,
    )
    val gitHubService = mockk<GitHubService>()


    "unhappy path" - {
        "AlreadyTaken - comment failure" {
            // given
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

        "NotApproved - comment failure" {
            // given
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

        "AssignIssue - comment failure" {
            // given
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

        "AssignIssue - assign failure" {
            // given
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
    }
})