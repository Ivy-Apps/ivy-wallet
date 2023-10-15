package ivy.automate.issue

import arrow.core.left
import arrow.core.right
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import ivy.automate.base.github.GitHubService
import ivy.automate.base.github.model.GitHubComment
import ivy.automate.base.github.model.GitHubIssue
import ivy.automate.base.github.model.GitHubIssueNumber
import ivy.automate.base.github.model.GitHubLabel
import ivy.automate.base.github.model.GitHubLabelName
import ivy.automate.base.github.model.GitHubPAT
import ivy.automate.base.github.model.GitHubUser
import ivy.automate.base.github.model.GitHubUsername

class DetermineActionTest : FreeSpec({
    val user1 = GitHubUser(GitHubUsername("user1"))
    val user2 = GitHubUser(GitHubUsername("user2"))
    val ivyBot = GitHubUser(GitHubUsername(Constants.IVY_BOT_USERNAME))

    val issueNumber = GitHubIssueNumber("1234")
    val args = Args(
        pat = GitHubPAT("pat"),
        issueNumber = issueNumber
    )
    val gitHubService = mockk<GitHubService>()

    suspend fun testScope(
        block: suspend context(GitHubService) () -> Unit
    ) {
        block(gitHubService)
    }

    fun label(name: String) = GitHubLabel(GitHubLabelName(name))

    "unhappy path" - {
        testScope {
            "fails to fetch comments" {
                // given
                coEvery {
                    gitHubService.fetchIssueComments(issueNumber)
                } returns Exception("API error").left()
                coEvery {
                    gitHubService.fetchIssue(issueNumber)
                } returns GitHubIssue(assignee = null).right()
                coEvery {
                    gitHubService.fetchIssueLabels(issueNumber)
                } returns listOf(label("approved")).right()

                // when
                val action = determineAction(args)

                // then
                action.shouldBeLeft()
            }

            "fails to fetch issue" {
                // given
                coEvery {
                    gitHubService.fetchIssueComments(issueNumber)
                } returns listOf(
                    GitHubComment(user1, "I'm on it")
                ).right()
                coEvery {
                    gitHubService.fetchIssue(issueNumber)
                } returns Exception("API error").left()
                coEvery {
                    gitHubService.fetchIssueLabels(issueNumber)
                } returns listOf(label("approved")).right()

                // when
                val action = determineAction(args)

                // then
                action.shouldBeLeft()
            }

            "fails to fetch labels" {
                // given
                coEvery {
                    gitHubService.fetchIssueComments(issueNumber)
                } returns listOf(
                    GitHubComment(user1, "I'm on it")
                ).right()
                coEvery {
                    gitHubService.fetchIssue(issueNumber)
                } returns GitHubIssue(assignee = null).right()
                coEvery {
                    gitHubService.fetchIssueLabels(issueNumber)
                } returns Exception("API error").left()

                // when
                val action = determineAction(args)

                // then
                action.shouldBeLeft()
            }
        }
    }

    "happy path" - {
        testScope {
            "AssignIssue - single comment" {
                // given
                coEvery {
                    gitHubService.fetchIssueComments(issueNumber)
                } returns listOf(
                    GitHubComment(user1, "I'm on it")
                ).right()
                coEvery {
                    gitHubService.fetchIssue(issueNumber)
                } returns GitHubIssue(assignee = null).right()
                coEvery {
                    gitHubService.fetchIssueLabels(issueNumber)
                } returns listOf(label("approved")).right()

                // when
                val action = determineAction(args)

                // then
                action.shouldBeRight() shouldBe Action.AssignIssue(issueNumber, user1)
            }

            "AssignIssue - multiple comments" {
                // given
                coEvery {
                    gitHubService.fetchIssueComments(issueNumber)
                } returns listOf(
                    GitHubComment(user1, "Can I take it?"),
                    GitHubComment(ivyBot, "Random message"),
                    GitHubComment(user2, "I'm on it")
                ).right()
                coEvery {
                    gitHubService.fetchIssue(issueNumber)
                } returns GitHubIssue(assignee = null).right()
                coEvery {
                    gitHubService.fetchIssueLabels(issueNumber)
                } returns listOf(label("approved")).right()

                // when
                val action = determineAction(args)

                // then
                action.shouldBeRight() shouldBe Action.AssignIssue(issueNumber, user2)
            }

            "AlreadyTaken" {
                // given
                coEvery {
                    gitHubService.fetchIssueComments(issueNumber)
                } returns listOf(
                    GitHubComment(user2, "I'm on it")
                ).right()
                coEvery {
                    gitHubService.fetchIssue(issueNumber)
                } returns GitHubIssue(assignee = user1).right()
                coEvery {
                    gitHubService.fetchIssueLabels(issueNumber)
                } returns listOf(label("approved")).right()

                // when
                val action = determineAction(args)

                // then
                action.shouldBeRight() shouldBe Action.AlreadyTaken(user2, issueNumber, user1)
            }

            "NotApproved" {
                // given
                coEvery {
                    gitHubService.fetchIssueComments(issueNumber)
                } returns listOf(
                    GitHubComment(user1, "I'm on it")
                ).right()
                coEvery {
                    gitHubService.fetchIssue(issueNumber)
                } returns GitHubIssue(assignee = null).right()
                coEvery {
                    gitHubService.fetchIssueLabels(issueNumber)
                } returns listOf(label("feature")).right()

                // when
                val action = determineAction(args)

                // then
                action.shouldBeRight() shouldBe Action.NotApproved(user1, issueNumber)
            }

            "DoNothing - empty comments" {
                // given
                coEvery {
                    gitHubService.fetchIssueComments(issueNumber)
                } returns emptyList<GitHubComment>().right()
                coEvery {
                    gitHubService.fetchIssue(issueNumber)
                } returns GitHubIssue(assignee = null).right()
                coEvery {
                    gitHubService.fetchIssueLabels(issueNumber)
                } returns listOf(label("approved")).right()

                // when
                val action = determineAction(args)

                // then
                action.shouldBeRight() shouldBe Action.DoNothing(issueNumber)
            }

            "DoNothing - ivy bot comment" {
                // given
                coEvery {
                    gitHubService.fetchIssueComments(issueNumber)
                } returns listOf(
                    GitHubComment(user1, "I'm on it"),
                    GitHubComment(user2, "I'm on it"),
                    GitHubComment(ivyBot, "Okay, I'm on it"),
                ).right()
                coEvery {
                    gitHubService.fetchIssue(issueNumber)
                } returns GitHubIssue(assignee = null).right()
                coEvery {
                    gitHubService.fetchIssueLabels(issueNumber)
                } returns listOf(label("approved")).right()

                // when
                val action = determineAction(args)

                // then
                action.shouldBeRight() shouldBe Action.DoNothing(issueNumber)
            }

            "DoNothing - unknown comment intention" {
                // given
                coEvery {
                    gitHubService.fetchIssueComments(issueNumber)
                } returns listOf(
                    GitHubComment(user1, "Random comment")
                ).right()
                coEvery {
                    gitHubService.fetchIssue(issueNumber)
                } returns GitHubIssue(assignee = null).right()
                coEvery {
                    gitHubService.fetchIssueLabels(issueNumber)
                } returns listOf(label("approved")).right()

                // when
                val action = determineAction(args)

                // then
                action.shouldBeRight() shouldBe Action.DoNothing(issueNumber)
            }
        }
    }
})