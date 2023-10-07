package ivy.automate.issue

import arrow.core.Either
import arrow.core.raise.either
import ivy.automate.base.IvyError
import ivy.automate.base.github.GitHubIssueNumber
import ivy.automate.base.github.GitHubService
import ivy.automate.base.github.GitHubServiceImpl
import ivy.automate.base.github.UserDto
import ivy.automate.base.ktor.KtorClientScope
import ivy.automate.base.ktor.ktorClientScope
import kotlinx.coroutines.runBlocking

data class Context(
    val gitHubService: GitHubService,
) : GitHubService by gitHubService

fun main(args: Array<String>): Unit = runBlocking {
    ktorClientScope {
        val context = Context(
            gitHubService = GitHubServiceImpl(),
        )
        with(context) {
            val result = execute(args.toList()).fold(
                ifLeft = { throw IvyError("TASK FAILED: $it") },
                ifRight = { "TASK SUCCESSFUL: $it" }
            )
            println("[ISSUE-ASSIGN] $result")
        }
    }
}

context(GitHubService, KtorClientScope)
private suspend fun execute(argsList: List<String>): Either<String, String> = either {
    val args = parseArgs(argsList).bind()
    val issueNumber = args.issueId

    val assignee = issueAssignee(issueNumber).bind()
    if(assignee != null) {
        return@either "Issue #${issueNumber.value} is already assigned to @${assignee.login}."
    }

    val approved = checkIssueApproved(issueNumber).bind()
    if (!approved) {
        return@either "Issue #${issueNumber.value} is not approved."
    }

    val comments = fetchComments(issueNumber)
        .mapLeft { "Failed to fetch comments: $it." }
        .bind()
    comments.joinToString("\n")
}

context(GitHubService, KtorClientScope)
private suspend fun issueAssignee(
    issueNumber: GitHubIssueNumber
): Either<String, UserDto?> = either {
    val issueInfo = fetchIssue(issueNumber)
        .mapLeft { "Failed to fetch issue: $it." }
        .bind()

    issueInfo.assignee
}

context(GitHubService, KtorClientScope)
private suspend fun checkIssueApproved(
    issueNumber: GitHubIssueNumber
): Either<String, Boolean> = either {
    val labels = fetchLabels(issueNumber)
        .mapLeft { "Failed to fetch labels: $it." }
        .bind()

    val isApproved = labels.any { it.name == "approved" }
    isApproved
}