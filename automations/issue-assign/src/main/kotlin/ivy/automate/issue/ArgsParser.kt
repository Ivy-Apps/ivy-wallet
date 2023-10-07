package ivy.automate.issue

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import ivy.automate.base.IvyDsl
import ivy.automate.base.github.model.GitHubIssueNumber
import ivy.automate.base.github.model.GitHubPAT

data class Args(
    val pat: GitHubPAT,
    val issueNumber: GitHubIssueNumber
)

fun parseArgs(argsList: List<String>): Either<String, Args> = either {
    val args = argsList.parseAsMap()

    val gitHubPAT = args.ensureArgument(Constants.ARG_GITHUB_PAT)
    val issueId = args.ensureArgument(Constants.ARG_ISSUE_NUMBER)

    Args(
        pat = GitHubPAT.from(gitHubPAT).bind(),
        issueNumber = GitHubIssueNumber.from(issueId).bind(),
    )
}

private fun List<String>.parseAsMap(): Map<String, String> {
    return mapNotNull { arg ->
        val values = arg.split("=")
            .takeIf { it.size == 2 } ?: return@mapNotNull null
        values[0] to values[1]
    }.toMap()
}

context(Raise<String>)
@IvyDsl
private fun Map<String, String>.ensureArgument(key: String): String {
    val value = this[key]
    ensureNotNull(value) {
        "Argument '$key' is missing."
    }
    return value
}