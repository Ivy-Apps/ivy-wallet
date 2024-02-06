package ivy.automate.compose.stability

import arrow.core.Either
import arrow.core.identity
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.right
import ivy.automate.compose.stability.model.ComposableArgument
import ivy.automate.compose.stability.model.UnstableComposable
import java.io.File

const val ComposeReportFolderName = "compose_compiler"

fun main() {
    val unstableComposables = findComposeReportFolders()
        .flatMap { reportFolder ->
            unstableComposables(reportFolder).fold(
                ifRight = ::identity,
                ifLeft = { error ->
                    println(error)
                    emptyList()
                }
            )
        }.toList()
    println(unstableComposables.filter { it.unstableArguments.isEmpty() })
}

private fun findComposeReportFolders(): Sequence<File> {
    val rootDirRelativePath = "../../"
    return File(rootDirRelativePath).walk()
        .filter { it.isDirectory && it.name == ComposeReportFolderName }
}

private fun unstableComposables(
    reportFolder: File
): Either<String, List<UnstableComposable>> = either {
    val files = reportFolder.listFiles()
        ?: raise("Empty report folder '${reportFolder.absoluteFile}'")
    val composablesTxt = files.firstOrNull {
        it.name.endsWith("composables.txt")
    }?.readText() ?: raise("Couldn't find '*composables.txt' in '${reportFolder.absoluteFile}'")
    val composablesCsv = files.firstOrNull {
        it.name.endsWith("composables.csv")
    } ?: raise("Couldn't find '*composables.csv' in '${reportFolder.absoluteFile}'")

    parseUnstableComposables(composablesCsv).bind().map {
        it.copy(
            unstableArguments = it.findUnstableArguments(composablesTxt)
        )
    }
}

private fun parseUnstableComposables(
    composablesCsv: File
): Either<String, List<UnstableComposable>> =
    catch({
        composablesCsv.readText()
            .split("\n") // rows
            .drop(1) // drop the header
            .filter { it.isNotBlank() }
            .map { row ->
                val values = row.split(",")
                val fullyQualifiedName = values[0]
                val name = values[1]
                val skippable = values[3].toInt() == 1
                val restartable = values[4].toInt() == 1

                UnstableComposable(
                    fullyQualifiedName = fullyQualifiedName,
                    name = name,
                    restartable = restartable,
                    skippable = skippable,
                    unstableArguments = emptyList(),
                )
            }
            .filter { !it.restartable || !it.skippable }
            .right()
    }) {
        Either.Left("CSV parse error for '${composablesCsv.path}': ${it.message}")
    }

private fun UnstableComposable.findUnstableArguments(
    composablesTxt: String
): List<ComposableArgument> {
    val composableFunction = composablesTxt.split(")\n").firstOrNull { funTxt ->
        "fun $name(" in funTxt
    } ?: return emptyList()
    return composableFunction.split("\n")
        .drop(1) // drop the signature
        .mapNotNull { paramTxt ->
            try {
                if ("unstable" in paramTxt) {
                    val words = paramTxt.split(" ")
                        .filter { it.isNotBlank() }
                    ComposableArgument(
                        name = words[1].dropLast(1), // drop the ":"
                        type = words[2]
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}