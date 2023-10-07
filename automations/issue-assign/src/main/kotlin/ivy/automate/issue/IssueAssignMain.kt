package ivy.automate.issue

import arrow.core.Either
import arrow.core.raise.either
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = runBlocking {
    val result = execute(args.toList()).fold(
        ifLeft = { "TASK FAILED: $it" },
        ifRight = { "TASK SUCCESSFUL: $it" }
    )
    println("[ISSUE-ASSIGN] $result")
}

private suspend fun execute(args: List<String>): Either<String, String> = either {

    "Assigned to..."
}