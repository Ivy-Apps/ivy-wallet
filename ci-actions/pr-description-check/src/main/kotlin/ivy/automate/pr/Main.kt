package ivy.automate.pr

import arrow.core.raise.catch
import kotlinx.coroutines.runBlocking
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun main(args: Array<String>): Unit = runBlocking {
    require(args.size == 1) { "CI error: Missing PR description argument" }
    val description = catch({ String(Base64.decode(args.first())) }) { e ->
        throw IllegalArgumentException("CI error: Base 64 decoding failed! $e")
    }
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
        throw PRDescriptionError(
            buildString {
                append("\nWe found problems in your PR (Pull Request) description. ")
                append("Please, follow our PR template:\n")
                append("https://github.com/Ivy-Apps/ivy-wallet/blob/main/.github/PULL_REQUEST_TEMPLATE.md\n")
                problems.forEach {
                    append(it)
                    append("\n\n")
                }
            }
        )
    }

    println("All good! The PR description looks fine.")
}

class PRDescriptionError(msg: String) : Exception(msg)