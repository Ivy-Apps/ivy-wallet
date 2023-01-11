package com.ivy.settings

import arrow.core.Either
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.settings.applocked.AppLockedFlow
import com.ivy.core.domain.action.settings.applocked.WriteAppLockedAct
import com.ivy.core.domain.action.settings.balance.HideBalanceFlow
import com.ivy.core.domain.action.settings.balance.WriteHideBalanceAct
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.domain.action.settings.basecurrency.WriteBaseCurrencyAct
import com.ivy.core.domain.action.settings.startdayofmonth.StartDayOfMonthFlow
import com.ivy.core.domain.action.settings.startdayofmonth.WriteStartDayOfMonthAct
import com.ivy.drive.google_drive.GoogleDriveService
import com.ivy.drive.google_drive.data.GoogleDriveError
import com.ivy.navigation.Navigator
import com.ivy.old.ImportOldJsonBackupAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
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
    private val importOldJsonBackupAct: ImportOldJsonBackupAct,
    private val googleDriveService: GoogleDriveService
) : SimpleFlowViewModel<SettingsState, SettingsEvent>() {
    override val initialUi: SettingsState = SettingsState(
        baseCurrency = "",
        startDayOfMonth = 1,
        hideBalance = false,
        appLocked = false,
        driveMounted = false
    )


    override val uiFlow: Flow<SettingsState> = combine(
        baseCurrencyFlow(),
        startDayOfMonthFlow(),
        hideBalanceFlow(Unit),
        appLockedFlow(Unit),
        googleDriveService.driveMounted,
    ) { baseCurrency, startDayOfMonth, hideBalance, appLocked, driveMounted ->
        SettingsState(
            baseCurrency = baseCurrency,
            startDayOfMonth = startDayOfMonth,
            hideBalance = hideBalance,
            appLocked = appLocked,
            driveMounted = driveMounted,
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
            is SettingsEvent.HideBalance -> {
                writeHideBalanceAct(event.hideBalance)
            }
            is SettingsEvent.AppLocked -> {
                writeAppLockedAct(event.appLocked)
            }
            is SettingsEvent.ImportOldData -> handleImportOldData(event)
            is SettingsEvent.MountDrive -> handleMountDrive()
        }
    }

    private suspend fun handleImportOldData(event: SettingsEvent.ImportOldData) {
        val result = importOldJsonBackupAct(event.jsonZipUri)
        Timber.d("Import data result: $result")
    }

    private suspend fun handleMountDrive() {
        if (googleDriveService.driveMounted.value) {
            withContext(Dispatchers.IO) {
                val result = googleDriveService.write(
                    path = Path("Ivy-Wallet-Debug-sync-folder/Backup/test.txt"),
                    content = LocalDateTime.now().toString()
                )
                when (result) {
                    is Either.Left -> Timber.d(
                        "Error writing to drive: ${
                            when (val error = result.value) {
                                is GoogleDriveError.IOError -> error.exception.also {
                                    it.printStackTrace()
                                }.message
                                GoogleDriveError.NotMounted -> "Not mounted!!!"
                            }
                        }"
                    )
                    is Either.Right -> Timber.d("Successfully wrote to drive")
                }
            }
        } else {
            googleDriveService.connect()
        }
    }

}