package ivy.automate.compose.stability

import java.io.File

const val ComposeReportFolderName = "compose_compiler"

fun main() {
    val rootDirRelativePath = "../../"
    File(rootDirRelativePath).walk()
        .filter { it.isDirectory && it.name == ComposeReportFolderName }
        .forEach { reportFolder ->
            println(reportFolder.listFiles().map { it.name })
        }
}