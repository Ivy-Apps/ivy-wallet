package com.ivy.old

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import com.ivy.common.toNonEmptyList
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.account.WriteAccountsAct
import com.ivy.core.domain.action.category.WriteCategoriesAct
import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.action.transaction.transfer.WriteTransferAct
import com.ivy.file.readFile
import com.ivy.file.unzip
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

class ImportOldJsonBackupAct @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val writeAccountsAct: WriteAccountsAct,
    private val writeCategoriesAct: WriteCategoriesAct,
    private val writeTrnsAct: WriteTrnsAct,
    private val writeTransferAct: WriteTransferAct,
) : Action<Uri, Either<ImportOldDataError, String>>() {
    override suspend fun Uri.willDo(): Either<ImportOldDataError, String> = either {
        // region Unzip
        val files = unzipBackupFile(
            zipFilePath = this@willDo
        ).bind()

        val backupJsonString = readBackupJson(files).bind()
        // endregion

        // region Parse
        val backup = parse(backupJsonString).bind()
        // TODO: Parse
        // endregion


        "Success".right().bind()
    }

    // region Unzip
    private fun unzipBackupFile(
        zipFilePath: Uri
    ): Either<ImportOldDataError, NonEmptyList<File>> {
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
            ?: return ImportOldDataError.UnzipFailed.left()

        unzippedFolder.delete()

        return unzippedFiles.right()
    }

    private fun readBackupJson(
        files: NonEmptyList<File>
    ): Either<ImportOldDataError, String> {
        fun hasJsonExtension(file: File): Boolean {
            val name = file.name
            val lastIndexOf = name.lastIndexOf(".")
                .takeIf { it != -1 } ?: return false
            return (name.substring(lastIndexOf).equals(".json", true))
        }

        val jsonFiles = files.filter(::hasJsonExtension)
        if (jsonFiles.size != 1)
            return ImportOldDataError.UnexpectedBackupZipFormat.left()

        return readFile(
            context,
            jsonFiles.first().toUri(),
            Charsets.UTF_16
        )?.right() ?: ImportOldDataError.FailedToReadJsonFile.left()
    }
    // endregion

    // region Parse
    private fun parse(jsonString: String): Either<ImportOldDataError, JSONObject> =
        Either.catch({ ImportOldDataError.FailedToParseJson(it) }) {
            JSONObject(jsonString)
        }
    // endregion

}

sealed interface ImportOldDataError {
    object UnzipFailed : ImportOldDataError
    object UnexpectedBackupZipFormat : ImportOldDataError
    object FailedToReadJsonFile : ImportOldDataError
    data class FailedToParseJson(val reason: Throwable) : ImportOldDataError
}