package ivy.automate.issue.create

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import ivy.automate.base.IvyError
import ivy.automate.base.github.GitHubIssueArgs
import ivy.automate.base.github.GitHubService
import ivy.automate.base.github.GitHubServiceImpl
import ivy.automate.base.github.model.NotBlankTrimmedString
import ivy.automate.base.github.parseArgs
import ivy.automate.base.ktor.ktorClientScope
import kotlinx.coroutines.runBlocking

data class Context(
    val gitHubService: GitHubService,
) : GitHubService by gitHubService

fun main(args: Array<String>): Unit = runBlocking {
    ktorClientScope {
        val context = Context(
            gitHubService = GitHubServiceImpl(
                ktorClient = ktorClient,
            ),
        )
        with(context) {
            val result = execute(args).fold(
                ifLeft = { throw IvyError("TASK FAILED: $it") },
                ifRight = { "TASK SUCCESSFUL: $it" }
            )
            println("[ISSUE-ASSIGN] $result")
        }
    }
}

context(GitHubService)
private suspend fun execute(argsArr: Array<String>): Either<String, String> = either {
    val args = parseArgs(argsArr.toList()).bind()
    comment(args, text = buildString {

    })
}

context(Raise<String>, GitHubService)
private suspend fun comment(
    args: GitHubIssueArgs,
    text: String
): String {
    return commentIssue(
        pat = args.pat,
        issueNumber = args.issueNumber,
        text = NotBlankTrimmedString(text)
    ).mapLeft {
        "Failed to comment: $it"
    }.map {
        "Commented on Issue #${args.issueNumber.value}."
    }.bind()
}