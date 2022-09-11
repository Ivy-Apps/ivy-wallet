package com.ivy.import_data

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.frp.test.TestIdlingResource
import com.ivy.frp.view.navigation.Navigation
import com.ivy.onboarding.viewmodel.OnboardingViewModel
import com.ivy.screens.Import
import com.ivy.wallet.domain.deprecated.logic.csv.CSVImporter
import com.ivy.wallet.domain.deprecated.logic.csv.CSVMapper
import com.ivy.wallet.domain.deprecated.logic.csv.CSVNormalizer
import com.ivy.wallet.domain.deprecated.logic.csv.IvyFileReader
import com.ivy.wallet.domain.deprecated.logic.csv.model.ImportApp
import com.ivy.wallet.domain.deprecated.logic.csv.model.ImportResult
import com.ivy.wallet.domain.deprecated.logic.zip.ExportZipLogic
import com.ivy.wallet.utils.asLiveData
import com.ivy.wallet.utils.getFileName
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.uiThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val ivyContext: com.ivy.core.ui.temp.IvyWalletCtx,
    private val nav: Navigation,
    private val fileReader: IvyFileReader,
    private val csvNormalizer: CSVNormalizer,
    private val csvMapper: CSVMapper,
    private val csvImporter: CSVImporter,
    private val exportZipLogic: ExportZipLogic
) : ViewModel() {
    private val _importStep = MutableLiveData<ImportStep>()
    val importStep = _importStep.asLiveData()

    private val _importApp = MutableLiveData<ImportApp>()
    val importType = _importApp.asLiveData()

    private val _importProgressPercent = MutableLiveData<Int>()
    val importProgressPercent = _importProgressPercent.asLiveData()

    private val _importResult = MutableLiveData<ImportResult>()
    val importResult = _importResult.asLiveData()

    fun start(screen: Import) {
        nav.onBackPressed[screen] = {
            when (importStep.value) {
                ImportStep.IMPORT_FROM -> false
                ImportStep.INSTRUCTIONS -> {
                    _importStep.value = ImportStep.IMPORT_FROM
                    true
                }
                ImportStep.LOADING -> {
                    //do nothing, disable back
                    true
                }
                ImportStep.RESULT -> {
                    _importStep.value = ImportStep.IMPORT_FROM
                    true
                }
                null -> false
            }
        }
    }

    @ExperimentalStdlibApi
    fun uploadFile(context: Context) {
        val importType = importType.value ?: return

        ivyContext.openFile { fileUri ->
            viewModelScope.launch {
                TestIdlingResource.increment()

                _importStep.value = ImportStep.LOADING

                _importResult.value = if (hasCSVExtension(context, fileUri))
                    restoreCSVFile(fileUri = fileUri, importApp = importType)
                else {
                    exportZipLogic.import(
                        context = context,
                        zipFileUri = fileUri,
                        onProgress = { progressPercent ->
                            uiThread {
                                _importProgressPercent.value =
                                    (progressPercent * 100).roundToInt()
                            }
                        })
                }

                _importStep.value = ImportStep.RESULT

                TestIdlingResource.decrement()
            }
        }
    }

    @ExperimentalStdlibApi
    private suspend fun restoreCSVFile(fileUri: Uri, importApp: ImportApp): ImportResult {
        return ioThread {
            val rawCSV = fileReader.read(
                uri = fileUri,
                charset = when (importApp) {
                    ImportApp.IVY -> Charsets.UTF_16
                    else -> Charsets.UTF_8
                }
            )
            if (rawCSV == null || rawCSV.isBlank()) {
                return@ioThread ImportResult(
                    rowsFound = 0,
                    transactionsImported = 0,
                    accountsImported = 0,
                    categoriesImported = 0,
                    failedRows = emptyList()
                )
            }

            val normalizedCSV = csvNormalizer.normalize(
                rawCSV = rawCSV,
                importApp = importApp
            )

            val mapping = csvMapper.mapping(
                type = importApp,
                headerRow = normalizedCSV.split("\n").getOrNull(0)
            )

            return@ioThread try {
                val result = csvImporter.import(
                    csv = normalizedCSV,
                    rowMapping = mapping,
                    onProgress = { progressPercent ->
                        uiThread {
                            _importProgressPercent.value =
                                (progressPercent * 100).roundToInt()
                        }
                    }
                )

                if (result.failedRows.isNotEmpty()) {
                    Timber.e("Import failed rows: ${result.failedRows}")
                }

                result
            } catch (e: Exception) {
                e.printStackTrace()
                ImportResult(
                    rowsFound = 0,
                    transactionsImported = 0,
                    accountsImported = 0,
                    categoriesImported = 0,
                    failedRows = emptyList()
                )
            }
        }
    }

    fun setImportType(importApp: ImportApp) {
        _importApp.value = importApp
        _importStep.value = ImportStep.INSTRUCTIONS
    }

    fun skip(
        screen: Import,
        onboardingViewModel: OnboardingViewModel
    ) {
        if (screen.launchedFromOnboarding) {
            onboardingViewModel.importSkip()
        }

        nav.back()
        resetState()
    }

    fun finish(
        screen: Import,
        onboardingViewModel: OnboardingViewModel
    ) {
        if (screen.launchedFromOnboarding) {
            val importSuccess = importResult.value?.transactionsImported?.let { it > 0 } ?: false
            onboardingViewModel.importFinished(
                success = importSuccess
            )
        }

        nav.back()
        resetState()
    }

    private fun resetState() {
        _importStep.value = ImportStep.IMPORT_FROM
    }

    private suspend fun hasCSVExtension(
        context: Context,
        fileUri: Uri
    ): Boolean = ioThread {
        val fileName = context.getFileName(fileUri)
        fileName?.endsWith(suffix = ".csv", ignoreCase = true) ?: false
    }
}