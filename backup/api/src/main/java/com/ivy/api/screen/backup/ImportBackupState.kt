package com.ivy.api.screen.backup

import androidx.compose.runtime.Immutable

@Immutable
data class ImportBackupState(
    val progress: Progress?,
    val result: ImportResult?,
)

@Immutable
data class Progress(
    val percent: Float,
    val message: String
)

@Immutable
sealed interface ImportResult {
    val message: String

    @Immutable
    data class Error(override val message: String) : ImportResult

    @Immutable
    data class Success(override val message: String) : ImportResult
}