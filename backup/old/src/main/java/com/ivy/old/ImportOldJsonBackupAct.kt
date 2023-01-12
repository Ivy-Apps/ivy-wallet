package com.ivy.old

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.computations.either
import arrow.core.left
import arrow.core.right
import com.ivy.backup.base.ImportSuccess
import com.ivy.backup.base.WriteBackupDataAct
import com.ivy.common.toNonEmptyList
import com.ivy.core.domain.action.Action
import com.ivy.file.readFile
import com.ivy.file.unzip
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

class ImportOldJsonBackupAct @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val parseOldJsonAct: ParseOldJsonAct,
    private val writeBackupDataAct: WriteBackupDataAct,
) : Action<Uri, Either<ImportOldDataError, ImportSuccess>>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(backupZipPath: Uri): Either<ImportOldDataError, ImportSuccess> =
        either {
            // region Unzip
            val files = unzipBackupZip(zipFilePath = backupZipPath).bind()
            val backupJsonString = readBackupJson(files).bind()
            // endregion

            // region Parse
            val backupJson = parse(backupJsonString).bind()
            val backupData = parseOldJsonAct(backupJson).bind()
            // endregion

            writeBackupDataAct(backupData)

            ImportSuccess(
                faultyTransfers = backupData.transfers.faulty
            )
        }

    // region Unzip
    private fun unzipBackupZip(
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

