package ivy.automate.issue

import arrow.core.Either
import arrow.core.raise.either
import ivy.automate.base.github.GitHubService
import ivy.automate.base.github.model.GitHubIssueNumber
import ivy.automate.base.github.model.GitHubUser

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
        val user: GitHubUser,
        override val issueNumber: GitHubIssueNumber
    ) : Action

    data class AlreadyTaken(
        val user: GitHubUser,
        override val issueNumber: GitHubIssueNumber,
        val assignee: GitHubUser,
    ) : Action
}

context(GitHubService)
suspend fun determineAction(args: Args): Either<String, Action> = either {
    val issueNumber = args.issueNumber
    val intention = checkCommentsForIntention(issueNumber).bind()
        ?: return@either Action.DoNothing(issueNumber)

    when (intention) {
        is CommentIntention.TakeIssue -> intention.toAction(issueNumber).bind()
        CommentIntention.Unknown -> Action.DoNothing(issueNumber)
    }
}

context(GitHubService)
private suspend fun CommentIntention.TakeIssue.toAction(
    issueNumber: GitHubIssueNumber,
): Either<String, Action> = either {
    val assignee = checkIfIssueIsAssigned(issueNumber).bind()
    if (assignee != null) {
        return@either Action.AlreadyTaken(user, issueNumber, assignee)
    }

    val approved = checkLabelsForApproved(issueNumber).bind()
    if (!approved) {
        return@either Action.NotApproved(user, issueNumber)
    }

    Action.AssignIssue(issueNumber, user)
}

context(GitHubService)
private suspend fun checkCommentsForIntention(
    issueNumber: GitHubIssueNumber
): Either<String, CommentIntention?> = either {
    val comments = fetchIssueComments(issueNumber)
        .mapLeft { "Failed to fetch comments: $it." }
        .bind()

    val lastComment = comments.lastOrNull() ?: return@either null
    if (lastComment.author.username.value == Constants.IVY_BOT_USERNAME) {
        // Do nothing for Ivy BOT comments
        return@either null
    }

    analyzeCommentIntention(lastComment)
}

context(GitHubService)
private suspend fun checkIfIssueIsAssigned(
    issueNumber: GitHubIssueNumber
): Either<String, GitHubUser?> = either {
    val issueInfo = fetchIssue(issueNumber)
        .mapLeft { "Failed to fetch issue: $it." }
        .bind()

    issueInfo.assignee
}

context(GitHubService)
private suspend fun checkLabelsForApproved(
    issueNumber: GitHubIssueNumber
): Either<String, Boolean> = either {
    val labels = fetchIssueLabels(issueNumber)
        .mapLeft { "Failed to fetch labels: $it." }
        .bind()

    val isApproved = labels.any { it.name.value == "approved" }
    isApproved
}