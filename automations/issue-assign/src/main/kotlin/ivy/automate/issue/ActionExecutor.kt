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
        append("⚠️ Hey @${user.username.value},")
        append(" this issue is already taken by @${assignee.username.value}.")
        append(" **Do not start working on it!**")
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
        append("⚠️ Hey @${user.username.value}")
        append(" this issue is not approved, yet.")
        append(" @${Constants.IVY_ADMIN} must approve it first.")
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
        append("Thank you for your interest @${user.username.value}! \uD83C\uDF89")
        append(" Assigned to you. You can work on it now ✅")
        append(" If you don't want to work on it now, please unassign yourself")
        append(" so other contributors can take it.")
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