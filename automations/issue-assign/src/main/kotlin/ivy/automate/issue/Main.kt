package ivy.automate.issue

import arrow.core.Either
import arrow.core.raise.either
import ivy.automate.base.IvyError
import ivy.automate.base.github.GitHubService
import ivy.automate.base.github.GitHubServiceImpl
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
    when (val action = determineAction(args).bind()) {
        is Action.AlreadyTaken -> action.execute(args).bind()
        is Action.AssignIssue -> action.execute(args).bind()
        is Action.NotApproved -> action.execute(args).bind()
        is Action.DoNothing -> "Do nothing."
    }
}