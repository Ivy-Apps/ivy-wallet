package com.ivy.backup.base

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import com.ivy.common.toNonEmptyList
import com.ivy.file.readFile
import com.ivy.file.unzip
import org.json.JSONObject
import java.io.File

suspend fun extractBackupJson(
    context: Context,
    backupFilePath: Uri
): Either<ImportBackupError, JSONObject> =
    either {
        // region Unzip
        val files = unzipBackupZip(context, zipFilePath = backupFilePath).bind()
        val backupJsonString = readBackupJson(context, files).bind()
        // endregion

        // region Parse
        parse(backupJsonString).bind()
    }


// region Unzip
private fun unzipBackupZip(
    context: Context,
    zipFilePath: Uri
): Either<ImportBackupError, NonEmptyList<File>> {
    val folderName = "backup" + System.currentTimeMillis()
    val unzippedFolder = File(context.cacheDir, folderName)

    unzip(
        context = context,
        zipFilePath = zipFilePath,
        unzipLocation = unzippedFolder
    )

    val unzippedFiles = unzippedFolder.listFiles()?.toList()
        ?.takeIf { it.isNotEmpty() }
        ?.toNonEmptyList()
        ?: return ImportBackupError.UnzipFailed(null).left()

    unzippedFolder.delete()

    return unzippedFiles.right()
}

private fun readBackupJson(
    context: Context,
    files: NonEmptyList<File>
): Either<ImportBackupError, String> {
    fun hasJsonExtension(file: File): Boolean {
        val name = file.name
        val lastIndexOf = name.lastIndexOf(".")
            .takeIf { it != -1 } ?: return false
        return (name.substring(lastIndexOf).equals(".json", true))
    }

    val jsonFiles = files.filter(::hasJsonExtension)
    if (jsonFiles.size != 1)
        return ImportBackupError.UnexpectedBackupZipFormat(null).left()

    return readFile(
        context,
        jsonFiles.first().toUri(),
        Charsets.UTF_16
    )?.right() ?: ImportBackupError.FailedToReadJsonFile(null).left()
}
// endregion

// region Parse
private fun parse(jsonString: String): Either<ImportBackupError, JSONObject> =
    Either.catch({ ImportBackupError.FailedToParseJson(it) }) {
        JSONObject(jsonString)
    }
// endregion