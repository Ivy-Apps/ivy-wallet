package ivy.automate.issue

import arrow.core.Either
import arrow.core.raise.either
import ivy.automate.base.github.GitHubService
import ivy.automate.base.github.model.NotBlankTrimmedString
import ivy.automate.base.ktor.KtorClientScope

context(GitHubService, KtorClientScope)
suspend fun Action.AlreadyTaken.execute(
    args: Args
): Either<String, String> = either {
    val commentText = buildString {
        append("Issue #${args.issueNumber} is already taken by @${assignee.username.value}.")
        val issuesUrl = "https://github.com/Ivy-Apps/ivy-wallet/issues"
        append(" Please, [pick another one]($issuesUrl).")
    }
    comment(args, commentText).bind()
    commentText
}

context(GitHubService, KtorClientScope)
suspend fun Action.NotApproved.execute(
    args: Args
): Either<String, String> = either {
    val commentText = buildString {
        append("Issue #${args.issueNumber} is not approved. ")
        append("@${Constants.IVY_ADMIN} must approve it first.")
    }
    comment(args, commentText).bind()
    commentText
}

context(GitHubService, KtorClientScope)
suspend fun Action.AssignIssue.execute(
    args: Args
): Either<String, String> = either {
    assignIssue(
        pat = args.pat,
        issueNumber = issueNumber,
        assignee = user.username
    ).mapLeft {
        "Failed to assign issue: $it"
    }.bind()

    val commentText = buildString {
        append("Thank you for your interest @${user.username.value}!")
        append(" Assigned to you. You should work on it now ✅")
        append(" If you don't want to work on it, please unassign yourself.")
    }
    comment(args, commentText).bind()
    commentText
}

context(GitHubService, KtorClientScope)
private suspend fun comment(
    args: Args,
    text: String
): Either<String, Unit> = either {
    commentIssue(
        pat = args.pat,
        issueNumber = args.issueNumber,
        text = NotBlankTrimmedString(text)
    ).mapLeft {
        "Failed to comment: $it"
    }
}