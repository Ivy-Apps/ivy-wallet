package com.ivy.api.screen.backup

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.ivy.backup.base.OnImportProgress
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.navigation.Navigator
import com.ivy.old.ImportOldJsonBackupAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportBackupViewModel @Inject constructor(
    private val navigator: Navigator,
    private val importOldJsonBackupAct: ImportOldJsonBackupAct,
) : SimpleFlowViewModel<ImportBackupState, ImportBackupEvent>() {
    override val initialUi = ImportBackupState(
        progress = null,
        result = null,
    )

    private val progress = MutableStateFlow(initialUi.progress)
    private val result = MutableStateFlow(initialUi.result)

    override val uiFlow: Flow<ImportBackupState> = combine(
        progress, result
    ) { progress, result ->
        ImportBackupState(
            progress = progress,
            result = result,
        )
    }


    // region Event Handling
    override suspend fun handleEvent(event: ImportBackupEvent): Unit = when (event) {
        ImportBackupEvent.Finish -> handleFinish()
        is ImportBackupEvent.ImportFile -> handleImportFile(event)
    }

    private fun handleFinish() {
        if (progress.value == null || result.value != null) {
            // nothing in progress or import finished
            navigator.back()
        }
    }

    private fun handleImportFile(event: ImportBackupEvent.ImportFile) {
        // Launch new scope so we won't block the event queue
        viewModelScope.launch {
            if (progress.value != null && result.value == null) return@launch

            result.value = null
            val res = importOldJsonBackupAct(
                ImportOldJsonBackupAct.Input(
                    backupZipPath = event.fileUri,
                    onProgress = object : OnImportProgress {
                        override fun onProgress(percent: Float, message: String) {
                            progress.value = Progress(percent, message)
                        }
                    }
                )
            )

            result.value = when (res) {
                is Either.Left -> ImportResult.Error(
                    res.value.reason?.message ?: res.value.toString()
                )
                is Either.Right -> ImportResult.Success(
                    message = "Success. Found faulty transfers: ${res.value.faultyTransfers}"
                )
            }
        }
    }
    // endregion
}