package com.ivy.old

sealed interface ImportOldDataError {
    val reason: Throwable?

    data class UnzipFailed(override val reason: Throwable?) : ImportOldDataError
    data class UnexpectedBackupZipFormat(override val reason: Throwable?) : ImportOldDataError
    data class FailedToReadJsonFile(override val reason: Throwable?) : ImportOldDataError
    data class FailedToParseJson(override val reason: Throwable) : ImportOldDataError

    sealed interface Parse : ImportOldDataError {
        data class Accounts(override val reason: Throwable) : Parse
        data class Categories(override val reason: Throwable) : Parse
        data class Transactions(override val reason: Throwable) : Parse
        data class Transfers(override val reason: Throwable) : Parse
        data class Settings(override val reason: Throwable) : Parse
    }
}