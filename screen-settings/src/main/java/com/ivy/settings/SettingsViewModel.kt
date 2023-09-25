package com.ivy.settings

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.core.ComposeViewModel
import com.ivy.core.RootScreen
import com.ivy.core.datamodel.legacy.Theme
import com.ivy.core.db.read.SettingsDao
import com.ivy.core.db.write.SettingsWriter
import com.ivy.core.util.refreshWidget
import com.ivy.frp.monad.Res
import com.ivy.frp.test.TestIdlingResource
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.LogoutLogic
import com.ivy.legacy.data.SharedPrefs
import com.ivy.legacy.domain.action.exchange.SyncExchangeRatesAct
import com.ivy.legacy.domain.action.settings.UpdateSettingsAct
import com.ivy.legacy.domain.deprecated.logic.zip.BackupLogic
import com.ivy.legacy.utils.formatNicelyWithTime
import com.ivy.legacy.utils.ioThread
import com.ivy.legacy.utils.timeNowUTC
import com.ivy.legacy.utils.uiThread
import com.ivy.wallet.domain.action.global.StartDayOfMonthAct
import com.ivy.wallet.domain.action.global.UpdateStartDayOfMonthAct
import com.ivy.wallet.domain.action.settings.SettingsAct
import com.ivy.wallet.domain.deprecated.logic.csv.ExportCSVLogic
import com.ivy.widget.balance.WalletBalanceWidgetReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDao: SettingsDao,
    @ApplicationContext
    private val context: Context,
    private val ivyContext: IvyWalletCtx,
    private val exportCSVLogic: ExportCSVLogic,
    private val logoutLogic: LogoutLogic,
    private val sharedPrefs: SharedPrefs,
    private val backupLogic: BackupLogic,
    private val startDayOfMonthAct: StartDayOfMonthAct,
    private val updateStartDayOfMonthAct: UpdateStartDayOfMonthAct,
    private val syncExchangeRatesAct: SyncExchangeRatesAct,
    private val settingsAct: SettingsAct,
    private val updateSettingsAct: UpdateSettingsAct,
    private val settingsWriter: SettingsWriter,
) : ComposeViewModel<SettingsState, SettingsEvent>() {

    private val currencyCode = mutableStateOf("")
    private val name = mutableStateOf("")
    private val currentTheme = mutableStateOf<Theme>(Theme.AUTO)
    private val lockApp = mutableStateOf(false)
    private val showNotifications = mutableStateOf(true)
    private val hideCurrentBalance = mutableStateOf(false)
    private val treatTransfersAsIncomeExpense = mutableStateOf(false)
    private val startDateOfMonth = mutableIntStateOf(1)
    private val progressState = mutableStateOf(false)

    @Composable
    override fun uiState(): SettingsState {
        return SettingsState(
            currencyCode = getCurrencyCode(),
            name = getName(),
            currentTheme = getCurrentTheme(),
            lockApp = getLockApp(),
            showNotifications = getShowNotifications(),
            hideCurrentBalance = getHideCurrentBalance(),
            treatTransfersAsIncomeExpense = getTreatTransfersAsIncomeExpense(),
            startDateOfMonth = getStartDateOfMonth(),
            progressState = getProgressState()
        )
    }

    private fun getCurrencyCode(): String {
        viewModelScope.launch {
            val settings = ioThread {
                settingsDao.findFirst()
            }

            currencyCode.value = settings.currency
        }

        return currencyCode.value
    }

    private fun getName(): String {
        viewModelScope.launch {
            val settings = ioThread {
                settingsDao.findFirst()
            }

            name.value = settings.name
        }

        return name.value
    }

    private fun getCurrentTheme(): Theme {
        viewModelScope.launch {
            currentTheme.value = settingsAct(Unit).theme
        }

        return currentTheme.value
    }

    private fun getLockApp(): Boolean {
        viewModelScope.launch {
            lockApp.value = sharedPrefs.getBoolean(SharedPrefs.APP_LOCK_ENABLED, false)
        }

        return lockApp.value
    }

    private fun getShowNotifications(): Boolean {
        viewModelScope.launch {
            showNotifications.value = sharedPrefs.getBoolean(
                SharedPrefs.SHOW_NOTIFICATIONS, true
            )
        }

        return showNotifications.value
    }

    private fun getHideCurrentBalance(): Boolean {
        viewModelScope.launch {
            hideCurrentBalance.value =
                sharedPrefs.getBoolean(SharedPrefs.HIDE_CURRENT_BALANCE, false)
        }

        return hideCurrentBalance.value
    }

    private fun getTreatTransfersAsIncomeExpense(): Boolean {
        viewModelScope.launch {
            treatTransfersAsIncomeExpense.value =
                sharedPrefs.getBoolean(SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE, false)
        }

        return treatTransfersAsIncomeExpense.value
    }

    private fun getStartDateOfMonth(): String {
        viewModelScope.launch {
            startDateOfMonth.intValue = startDayOfMonthAct(Unit)
        }

        return startDateOfMonth.value.toString()
    }

    private fun getProgressState(): Boolean {
        return progressState.value
    }

    private fun exportToZip(context: Context) {
        ivyContext.createNewFile(
            "Ivy Wallet (${
                timeNowUTC().formatNicelyWithTime(noWeekDay = true)
            }).zip"
        ) { fileUri ->
            viewModelScope.launch(Dispatchers.IO) {
                progressState.value = true
                backupLogic.exportToFile(zipFileUri = fileUri)
                progressState.value = false

                sharedPrefs.putBoolean(SharedPrefs.DATA_BACKUP_COMPLETED, true)
                ivyContext.dataBackupCompleted = true

                uiThread {
                    (context as RootScreen).shareZipFile(
                        fileUri = fileUri
                    )
                }
            }
        }
    }

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.SetCurrency -> setCurrency(event.newCurrency)
            is SettingsEvent.SetName -> setName(event.newName)
            SettingsEvent.ExportToCsv -> exportToCSV()
            SettingsEvent.BackupData -> // TODO
                SettingsEvent.SwitchTheme

            -> switchTheme()
            is SettingsEvent.SetLockApp -> setLockApp(event.lockApp)
            is SettingsEvent.SetShowNotifications -> setShowNotifications(event.showNotifications)
            is SettingsEvent.SetHideCurrentBalance -> setHideCurrentBalance(
                event.hideCurrentBalance
            )

            is SettingsEvent.SetTransfersAsIncomeExpense -> setTransfersAsIncomeExpense(
                event.treatTransfersAsIncomeExpense
            )

            is SettingsEvent.SetStartDateOfMonth -> setStartDateOfMonth(event.startDate)

            SettingsEvent.DeleteCloudUserData -> deleteCloudUserData()
            SettingsEvent.DeleteAllUserData -> deleteAllUserData()
        }
    }

    private fun setCurrency(newCurrency: String) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            ioThread {
                settingsWriter.save(
                    settingsDao.findFirst().copy(
                        currency = newCurrency
                    )
                )

                syncExchangeRatesAct(
                    SyncExchangeRatesAct.Input(
                        baseCurrency = newCurrency
                    )
                )
            }
            start()

            TestIdlingResource.decrement()
        }
    }

    private fun setName(newName: String) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            ioThread {
                settingsWriter.save(
                    settingsDao.findFirst().copy(
                        name = newName
                    )
                )
            }
            start()

            TestIdlingResource.decrement()
        }
    }

    fun exportToCSV() {
        ivyContext.createNewFile(
            "Ivy Wallet (${
                timeNowUTC().formatNicelyWithTime(noWeekDay = true)
            }).csv"
        ) { fileUri ->
            viewModelScope.launch {
                TestIdlingResource.increment()

                exportCSVLogic.exportToFile(
                    context = context,
                    fileUri = fileUri
                )

                (context as RootScreen).shareCSVFile(
                    fileUri = fileUri
                )

                TestIdlingResource.decrement()
            }
        }
    }

    private fun switchTheme() {
        viewModelScope.launch {
            val currentSettings = settingsAct(Unit)
            val newTheme = when (currentSettings.theme) {
                Theme.LIGHT -> Theme.DARK
                Theme.DARK -> Theme.AUTO
                Theme.AUTO -> Theme.LIGHT
            }
            updateSettingsAct(
                currentSettings.copy(
                    theme = newTheme
                )
            )
            ivyContext.switchTheme(newTheme)
            currentTheme.value = newTheme
        }
    }

    private fun setLockApp(lock: Boolean) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            sharedPrefs.putBoolean(SharedPrefs.APP_LOCK_ENABLED, lock)
            lockApp.value = lock
            refreshWidget(WalletBalanceWidgetReceiver::class.java)

            TestIdlingResource.decrement()
        }
    }

    private fun setShowNotifications(notificationsShow: Boolean) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            sharedPrefs.putBoolean(SharedPrefs.SHOW_NOTIFICATIONS, notificationsShow)
            showNotifications.value = notificationsShow

            TestIdlingResource.decrement()
        }
    }

    private fun setHideCurrentBalance(hideBalance: Boolean) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            sharedPrefs.putBoolean(SharedPrefs.HIDE_CURRENT_BALANCE, hideBalance)
            hideCurrentBalance.value = hideBalance

            TestIdlingResource.decrement()
        }
    }

    private fun setTransfersAsIncomeExpense(treatTransfersAsIncomeExpense: Boolean) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            sharedPrefs.putBoolean(
                SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE,
                this@SettingsViewModel.treatTransfersAsIncomeExpense.value
            )
            this@SettingsViewModel.treatTransfersAsIncomeExpense.value =
                treatTransfersAsIncomeExpense

            TestIdlingResource.decrement()
        }
    }

    private fun setStartDateOfMonth(startDate: Int) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            when (val res = updateStartDayOfMonthAct(startDate)) {
                is Res.Err -> {}
                is Res.Ok -> {
                    startDateOfMonth.intValue = res.data
                }
            }

            TestIdlingResource.decrement()
        }
    }

    private fun deleteCloudUserData() {
        viewModelScope.launch {
            cloudLogout()
        }
    }

    private fun cloudLogout() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            logoutLogic.cloudLogout()

            TestIdlingResource.decrement()
        }
    }

    private fun deleteAllUserData() {
        viewModelScope.launch {
            logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            logoutLogic.logout()

            TestIdlingResource.decrement()
        }
    }
}