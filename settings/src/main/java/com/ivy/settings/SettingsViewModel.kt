package com.ivy.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewModelScope
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.settings.applocked.AppLockedFlow
import com.ivy.core.domain.action.settings.applocked.WriteAppLockedAct
import com.ivy.core.domain.action.settings.balance.HideBalanceFlow
import com.ivy.core.domain.action.settings.balance.WriteHideBalanceAct
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.domain.action.settings.basecurrency.WriteBaseCurrencyAct
import com.ivy.core.domain.action.settings.startdayofmonth.StartDayOfMonthFlow
import com.ivy.core.domain.action.settings.startdayofmonth.WriteStartDayOfMonthAct
import com.ivy.core.domain.algorithm.accountcache.NukeAccountCacheAct
import com.ivy.core.domain.pure.util.combine
import com.ivy.core.ui.Toaster
import com.ivy.drive.google_drive.api.GoogleDriveConnection
import com.ivy.drive.google_drive.api.GoogleDriveService
import com.ivy.impl.export.BackupDataAct
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.Destination
import com.ivy.settings.data.BackupImportState
import com.ivy.settings.data.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.io.path.Path

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val navigator: Navigator,
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val writeBaseCurrencyAct: WriteBaseCurrencyAct,
    private val startDayOfMonthFlow: StartDayOfMonthFlow,
    private val writeStartDayOfMonthAct: WriteStartDayOfMonthAct,
    private val hideBalanceFlow: HideBalanceFlow,
    private val writeHideBalanceAct: WriteHideBalanceAct,
    private val appLockedFlow: AppLockedFlow,
    private val writeAppLockedAct: WriteAppLockedAct,
    private val googleDriveConnection: GoogleDriveConnection,
    private val googleDriveService: GoogleDriveService,
    private val nukeAccountCacheAct: NukeAccountCacheAct,
    private val backupDataAct: BackupDataAct,
    private val toaster: Toaster,
) : SimpleFlowViewModel<SettingsState, SettingsEvent>() {
    override val initialUi: SettingsState = SettingsState(
        baseCurrency = "",
        startDayOfMonth = 1,
        hideBalance = false,
        appLocked = false,
        driveMounted = false,
        importOldData = BackupImportState.Idle,
        supportedLanguages = enumValues<Language>().toList(),
        currentLanguage = AppCompatDelegate.getApplicationLocales()[0].toString()
    )

    private val importOldDataState = MutableStateFlow(initialUi.importOldData)

    private val currentLanguageState = MutableStateFlow(initialUi.currentLanguage)

    override val uiFlow: Flow<SettingsState> = combine(
        baseCurrencyFlow(),
        startDayOfMonthFlow(),
        hideBalanceFlow(Unit),
        appLockedFlow(Unit),
        googleDriveConnection.driveMounted,
        importOldDataState,
        currentLanguageState
    ) { baseCurrency, startDayOfMonth, hideBalance,
        appLocked, driveMounted, importOldData, currentLanguage ->
        SettingsState(
            baseCurrency = baseCurrency,
            startDayOfMonth = startDayOfMonth,
            hideBalance = hideBalance,
            appLocked = appLocked,
            driveMounted = driveMounted,
            importOldData = importOldData,
            supportedLanguages = initialUi.supportedLanguages,
            currentLanguage = currentLanguage
        )
    }

    override suspend fun handleEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.Back -> navigator.back()
            is SettingsEvent.BaseCurrencyChange -> {
                writeBaseCurrencyAct(event.newCurrency)
            }

            is SettingsEvent.StartDayOfMonth -> {
                writeStartDayOfMonthAct(event.startDayOfMonth)
            }

//            changing locale to the selected language {will fallback to default strings.xml file if language is not supported}
            is SettingsEvent.LanguageChange -> {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(event.languageCode)
                )
                currentLanguageState.value = event.languageCode
            }

            is SettingsEvent.ExchangeRates -> {
                handleExchangeRatesEvent()
            }

            is SettingsEvent.HideBalance -> {
                writeHideBalanceAct(event.hideBalance)
            }

            is SettingsEvent.AppLocked -> {
                writeAppLockedAct(event.appLocked)
            }

            SettingsEvent.ImportOldData -> handleImportOldData()
            is SettingsEvent.MountDrive -> handleMountDrive()
            SettingsEvent.AddFrame -> handleAddFrame()
            SettingsEvent.NukeAccCache -> {
                nukeAccountCacheAct(Unit)
            }

            is SettingsEvent.BackupData -> handleBackupData(event)
        }
    }

    private suspend fun handleImportOldData() {
        navigator.navigate(Destination.importBackup.destination(Unit))
    }

    private fun handleExchangeRatesEvent() {
        navigator.navigate(Destination.exchangeRates.destination(Unit))
    }

    private suspend fun handleMountDrive() {
        if (googleDriveConnection.driveMounted.value) {
            withContext(Dispatchers.IO) {
                val result = googleDriveService.write(
                    path = Path("Ivy-Wallet-Debug-sync-folder/Backup/test.txt"),
                    content = LocalDateTime.now().toString()
                )
                when (result) {
                    is Left -> Timber.d(
                        "Error writing to drive: ${
                            result.value.exception.also {
                                it.printStackTrace()
                            }.message
                        }"
                    )

                    is Right -> Timber.d("Successfully wrote to drive")
                }
            }
        } else {
            googleDriveConnection.connect()
        }
    }

    private fun handleAddFrame() {
        navigator.navigate(Destination.addFrame.destination(Unit))
    }


    var backupInProgress = false
    private suspend fun handleBackupData(event: SettingsEvent.BackupData) {
        if (backupInProgress) return
        viewModelScope.launch {
            backupInProgress = true
            when (val result = backupDataAct(BackupDataAct.Input(event.backupLocation))) {
                is Left -> {
                    result.value.reason?.printStackTrace()
                    toaster.show("Error: ${result.value.reason}")
                }

                is Right -> {
                    if (result.value.uploadedToDrive) {
                        toaster.show("Success! Data uploaded to your Google Drive.")
                    } else {
                        toaster.show("Local backup successful!")
                    }
                }
            }
            backupInProgress = false
        }
    }
}