package ivy.automate.compose.stability

import arrow.core.Either
import arrow.core.identity
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.right
import ivy.automate.compose.stability.model.UnstableComposable
import java.io.File

const val ComposeReportFolderName = "compose_compiler"

fun main() {
    findComposeReportFolders()
        .flatMap {
            it.reportFolder().fold(
                ifRight = ::identity,
                ifLeft = { error ->
                    println(error)
                    emptyList()
                }
            )
        }.forEach {
            println(it)
        }
}

private fun findComposeReportFolders(): Sequence<File> {
    val rootDirRelativePath = "../../"
    return File(rootDirRelativePath).walk()
        .filter { it.isDirectory && it.name == ComposeReportFolderName }
}

private fun File.reportFolder(): Either<String, List<UnstableComposable>> = either {
    val files = listFiles() ?: raise("Empty report folder '$absolutePath'")
    val composablesTxt = files.firstOrNull {
        it.name.endsWith("composables.txt")
    } ?: raise("Couldn't find '*composables.txt' in '$absolutePath'")
    val composablesCsv = files.firstOrNull {
        it.name.endsWith("composables.csv")
    } ?: raise("Couldn't find '*composables.csv' in '$absolutePath'")

    parseUnstableComposables(composablesCsv).bind()
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