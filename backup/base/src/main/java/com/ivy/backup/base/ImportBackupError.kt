package com.ivy.backup.base

sealed interface ImportBackupError {
    val reason: Throwable?

    data class UnzipFailed(override val reason: Throwable?) : ImportBackupError
    data class UnexpectedBackupZipFormat(override val reason: Throwable?) : ImportBackupError
    data class FailedToReadJsonFile(override val reason: Throwable?) : ImportBackupError
    data class FailedToParseJson(override val reason: Throwable) : ImportBackupError

    sealed interface Parse : ImportBackupError {
        data class Accounts(override val reason: Throwable) : Parse
        data class AccountFolders(override val reason: Throwable) : Parse
        data class Attachments(override val reason: Throwable) : Parse
        data class Categories(override val reason: Throwable) : Parse
        data class Transactions(override val reason: Throwable) : Parse
        data class Transfers(override val reason: Throwable) : Parse
        data class Settings(override val reason: Throwable) : Parse
    }
}