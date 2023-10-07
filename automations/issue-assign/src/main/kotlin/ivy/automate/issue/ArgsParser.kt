package ivy.automate.issue

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import ivy.automate.base.github.GitHubID

const val ARG_ISSUE_ID = "issueId"

data class Args(
    val issueId: GitHubID
)

fun parseArgs(args: List<String>): Either<String, Args> = either {
    val argsMap = args.parseAsMap()

    val issueId = argsMap[ARG_ISSUE_ID]
    ensureNotNull(issueId) {
        "Argument '$ARG_ISSUE_ID' is missing"
    }

    Args(
        issueId = GitHubID.from(issueId).bind(),
    )
}

private fun List<String>.parseAsMap(): Map<String, String> {
    return mapNotNull { arg ->
        val values = arg.split("=")
            .takeIf { it.size == 2 } ?: return@mapNotNull null
        values[0] to values[1]
    }.toMap()
}