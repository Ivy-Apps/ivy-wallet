package ivy.automate.issue

import arrow.core.Either
import arrow.core.raise.either
import ivy.automate.base.github.GitHubService
import ivy.automate.base.github.model.GitHubIssueNumber
import ivy.automate.base.github.model.GitHubUser
import ivy.automate.base.ktor.KtorClientScope

const val BOT_USERNAME = "ivywallet"

sealed interface Action {
    val issueNumber: GitHubIssueNumber

    data class DoNothing(
        override val issueNumber: GitHubIssueNumber
    ) : Action

    data class AssignIssue(
        override val issueNumber: GitHubIssueNumber,
        val user: GitHubUser
    ) : Action

    data class NotApproved(
        override val issueNumber: GitHubIssueNumber
    ) : Action

    data class AlreadyTaken(
        override val issueNumber: GitHubIssueNumber,
        val assignee: GitHubUser,
    ) : Action
}

context(GitHubService, KtorClientScope)
suspend fun determineAction(args: Args): Either<String, Action> = either {
    val issueNumber = args.issueId

    val intention = checkCommentsForIntention(issueNumber).bind()
        ?: return@either Action.DoNothing(issueNumber)

    when (intention) {
        is CommentIntention.TakeIssue -> intention.handle(issueNumber).bind()
        CommentIntention.Unknown -> Action.DoNothing(issueNumber)
    }
}

context(GitHubService, KtorClientScope)
private suspend fun CommentIntention.TakeIssue.handle(
    issueNumber: GitHubIssueNumber,
): Either<String, Action> = either {
    val assignee = checkIfIssueIsAssigned(issueNumber).bind()
    if (assignee != null) {
        return@either Action.AlreadyTaken(issueNumber, assignee)
    }

    val approved = checkLabelsForApproved(issueNumber).bind()
    if (!approved) {
        return@either Action.NotApproved(issueNumber)
    }

    Action.AssignIssue(issueNumber, user)
}

context(GitHubService, KtorClientScope)
private suspend fun checkCommentsForIntention(
    issueNumber: GitHubIssueNumber
): Either<String, CommentIntention?> = either {
    val comments = fetchIssueComments(issueNumber)
        .mapLeft { "Failed to fetch comments: $it." }
        .bind()

    val lastComment = comments.lastOrNull { it.author.username.value != BOT_USERNAME }
        ?: return@either null

    analyzeCommentIntention(lastComment)
}

context(GitHubService, KtorClientScope)
private suspend fun checkIfIssueIsAssigned(
    issueNumber: GitHubIssueNumber
): Either<String, GitHubUser?> = either {
    val issueInfo = fetchIssue(issueNumber)
        .mapLeft { "Failed to fetch issue: $it." }
        .bind()

    issueInfo.assignee
}

context(GitHubService, KtorClientScope)
private suspend fun checkLabelsForApproved(
    issueNumber: GitHubIssueNumber
): Either<String, Boolean> = either {
    val labels = fetchIssueLabels(issueNumber)
        .mapLeft { "Failed to fetch labels: $it." }
        .bind()

    val isApproved = labels.any { it.name.value == "approved" }
    isApproved
}