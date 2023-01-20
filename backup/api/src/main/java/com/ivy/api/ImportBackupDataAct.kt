package com.ivy.api

import android.content.Context
import android.net.Uri
import arrow.core.Either
import arrow.core.computations.either
import com.ivy.backup.base.ImportBackupError
import com.ivy.backup.base.OnImportProgress
import com.ivy.backup.base.WriteBackupDataAct
import com.ivy.backup.base.data.BackupData
import com.ivy.backup.base.data.ImportResult
import com.ivy.backup.base.extractBackupJson
import com.ivy.core.domain.action.Action
import com.ivy.impl.load.ParseV1JsonDataAct
import com.ivy.old.parse.ParseOldJsonAct
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject

class ImportBackupDataAct @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val parseOldJsonAct: ParseOldJsonAct,
    private val parseV1JsonDataAct: ParseV1JsonDataAct,
    private val writeBackupDataAct: WriteBackupDataAct,
) : Action<ImportBackupDataAct.Input, Either<ImportBackupError, ImportResult>>() {
    data class Input(
        val backupZipPath: Uri,
        val onProgress: OnImportProgress,
    )

    override suspend fun action(input: Input): Either<ImportBackupError, ImportResult> =
        either {
            val progress = { percent: Float, message: String ->
                input.onProgress.onProgress(percent, message)
            }

            progress(1f, "Extracting backup JSON...")
            val backupJson = extractBackupJson(context, input.backupZipPath).bind()
            progress(3f, "Parsing backup JSON...")
            val parser = determineParser(backupJson)
            val backupData = parser(backupJson).bind()

            progress(10f, "Backup JSON parsed. Saving to database...")
            writeBackupDataAct(
                WriteBackupDataAct.Input(
                    backup = backupData,
                    onProgress = object : OnImportProgress {
                        override fun onProgress(percent: Float, message: String) {
                            // Adjust from 13% to 100%
                            val adjustedPercent = 0.13f + (0.87f * percent)
                            progress(adjustedPercent, message)
                        }
                    }
                )
            )

            ImportResult(
                faultyTransfers = backupData.transfers.faulty
            )
        }

    private suspend fun determineParser(
        backupJson: JSONObject
    ): suspend (JSONObject) -> Either<ImportBackupError, BackupData> = when {
        backupJson.has("backupInfo") -> { json: JSONObject -> parseV1JsonDataAct(json) }
        else -> { json: JSONObject -> parseOldJsonAct(json) }
    }
}

