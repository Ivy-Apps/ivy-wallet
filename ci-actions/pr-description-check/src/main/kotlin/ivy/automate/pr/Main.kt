package ivy.automate.pr

import kotlinx.coroutines.runBlocking
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun main(args: Array<String>): Unit = runBlocking {
    if (args.size != 1) {
        error("CI error: Missing PR description argument")
    }
    val description = String(Base64.decode(args.first()))
    println("Analyzing PR description:")
    println(description)
    println("------")

    val analyzers = listOf<PRDescriptionAnalyzer>(
        ClosesIssueAnalyzer(),
    )

    val problems = analyzers.mapNotNull {
        it.analyze(description).leftOrNull()
    }
    if (problems.isNotEmpty()) {
        error(
            buildString {
                append("We found problems in your PR (Pull Request) description. ")
                append("Please, follow our PR template:")
                problems.forEach {
                    append(it)
                    append("\n\n")
                }
            }
        )
    }

    println("All good! The PR description looks fine.")
}

