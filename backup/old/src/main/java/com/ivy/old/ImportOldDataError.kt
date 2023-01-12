package com.ivy.old

sealed interface ImportOldDataError {
    object UnzipFailed : ImportOldDataError
    object UnexpectedBackupZipFormat : ImportOldDataError
    object FailedToReadJsonFile : ImportOldDataError
    data class FailedToParseJson(val reason: Throwable) : ImportOldDataError

    sealed interface Parse : ImportOldDataError {
        data class Accounts(val reason: Throwable) : Parse
        data class Categories(val reason: Throwable) : Parse
        data class Transactions(val reason: Throwable) : Parse
        data class Transfers(val reason: Throwable) : Parse
        data class Settings(val reason: Throwable) : Parse
    }
}