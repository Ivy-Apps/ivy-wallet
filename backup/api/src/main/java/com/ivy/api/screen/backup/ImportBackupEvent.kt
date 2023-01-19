package com.ivy.api.screen.backup

import android.net.Uri

sealed interface ImportBackupEvent {
    data class ImportFile(val fileUri: Uri) : ImportBackupEvent
    object Finish : ImportBackupEvent
}