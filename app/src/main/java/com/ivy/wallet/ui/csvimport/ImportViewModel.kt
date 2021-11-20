package com.ivy.wallet.ui.csvimport

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.TestIdlingResource
import com.ivy.wallet.base.asLiveData
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.base.uiThread
import com.ivy.wallet.logic.csv.CSVImporter
import com.ivy.wallet.logic.csv.CSVMapper
import com.ivy.wallet.logic.csv.CSVNormalizer
import com.ivy.wallet.logic.csv.IvyFileReader
import com.ivy.wallet.logic.csv.model.ImportResult
import com.ivy.wallet.logic.csv.model.ImportType
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.onboarding.viewmodel.OnboardingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val ivyContext: IvyContext,
    private val fileReader: IvyFileReader,
    private val csvNormalizer: CSVNormalizer,
    private val csvMapper: CSVMapper,
    private val csvImporter: CSVImporter
) : ViewModel() {
    private val _importStep = MutableLiveData<ImportStep>()
    val importStep = _importStep.asLiveData()

    private val _importType = MutableLiveData<ImportType>()
    val importType = _importType.asLiveData()

    private val _importProgressPercent = MutableLiveData<Int>()
    val importProgressPercent = _importProgressPercent.asLiveData()

    private val _importResult = MutableLiveData<ImportResult>()
    val importResult = _importResult.asLiveData()

    fun start(screen: Screen.Import) {
        ivyContext.onBackPressed[screen] = {
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

    fun uploadFile() {
        val importType = importType.value ?: return

        ivyContext.openFile { fileUri ->
            viewModelScope.launch {
                TestIdlingResource.increment()

                _importStep.value = ImportStep.LOADING

                _importResult.value = ioThread {
                    val rawCSV = fileReader.read(
                        uri = fileUri,
                        charset = when (importType) {
                            ImportType.IVY -> Charsets.UTF_16
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
                        importType = importType
                    )

                    val mapping = csvMapper.mapping(
                        type = importType,
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
                }!!

                _importStep.value = ImportStep.RESULT

                TestIdlingResource.decrement()
            }
        }
    }

    fun setImportType(importType: ImportType) {
        _importType.value = importType
        _importStep.value = ImportStep.INSTRUCTIONS
    }

    fun skip(
        screen: Screen.Import,
        onboardingViewModel: OnboardingViewModel
    ) {
        if (screen.launchedFromOnboarding) {
            onboardingViewModel.importSkip()
        }

        ivyContext.back()
        resetState()
    }

    fun finish(
        screen: Screen.Import,
        onboardingViewModel: OnboardingViewModel
    ) {
        if (screen.launchedFromOnboarding) {
            val importSuccess = importResult.value?.transactionsImported?.let { it > 0 } ?: false
            onboardingViewModel.importFinished(
                success = importSuccess
            )
        }

        ivyContext.back()
        resetState()
    }

    private fun resetState() {
        _importStep.value = ImportStep.IMPORT_FROM
    }
}