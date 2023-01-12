package com.ivy.old

sealed interface ImportOldDataError {
    object UnzipFailed : ImportOldDataError
    object UnexpectedBackupZipFormat : ImportOldDataError
    object FailedToReadJsonFile : ImportOldDataError
    data class FailedToParseJson(val reason: Throwable) : ImportOldDataError
}