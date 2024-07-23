package ivy.automate.pr

import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = runBlocking {
    if (args.size != 1) {
        error("CI error: Missing PR description argument")
    }
    val description = args.first()
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

