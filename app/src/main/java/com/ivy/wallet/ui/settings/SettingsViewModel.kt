package com.ivy.wallet.ui.settings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.design.l0_system.Theme
import com.ivy.frp.monad.Res
import com.ivy.frp.test.TestIdlingResource
import com.ivy.frp.view.navigation.Navigation
import com.ivy.wallet.domain.action.exchange.SyncExchangeRatesAct
import com.ivy.wallet.domain.action.global.StartDayOfMonthAct
import com.ivy.wallet.domain.action.global.UpdateStartDayOfMonthAct
import com.ivy.wallet.domain.action.settings.SettingsAct
import com.ivy.wallet.domain.action.settings.UpdateSettingsAct
import com.ivy.wallet.domain.data.core.User
import com.ivy.wallet.domain.deprecated.logic.LogoutLogic
import com.ivy.wallet.domain.deprecated.logic.csv.ExportCSVLogic
import com.ivy.wallet.domain.deprecated.logic.zip.BackupLogic
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.io.persistence.dao.UserDao
import com.ivy.wallet.refreshWidget
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.RootActivity
import com.ivy.wallet.ui.widget.WalletBalanceWidgetReceiver
import com.ivy.wallet.utils.OpResult
import com.ivy.wallet.utils.asLiveData
import com.ivy.wallet.utils.formatNicelyWithTime
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.sendToCrashlytics
import com.ivy.wallet.utils.timeNowUTC
import com.ivy.wallet.utils.uiThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDao: SettingsDao,
    private val userDao: UserDao,
    private val ivyContext: IvyWalletCtx,
    private val exportCSVLogic: ExportCSVLogic,
    private val logoutLogic: LogoutLogic,
    private val sharedPrefs: SharedPrefs,
    private val backupLogic: BackupLogic,
    private val startDayOfMonthAct: StartDayOfMonthAct,
    private val updateStartDayOfMonthAct: UpdateStartDayOfMonthAct,
    private val nav: Navigation,
    private val syncExchangeRatesAct: SyncExchangeRatesAct,
    private val settingsAct: SettingsAct,
    private val updateSettingsAct: UpdateSettingsAct,
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user = _user.asLiveData()

    private val _nameLocalAccount = MutableLiveData<String?>()
    val nameLocalAccount = _nameLocalAccount.asLiveData()

    private val _opSync = MutableLiveData<OpResult<Boolean>>()
    val opSync = _opSync.asLiveData()

    private val _currencyCode = MutableLiveData<String>()
    val currencyCode = _currencyCode.asLiveData()

    private val _currentTheme = MutableLiveData<Theme>()
    val currentTheme = _currentTheme.asLiveData()

    private val _lockApp = MutableLiveData<Boolean>()
    val lockApp = _lockApp.asLiveData()

    private val _hideCurrentBalance = MutableStateFlow(false)
    val hideCurrentBalance = _hideCurrentBalance.asStateFlow()

    private val _showNotifications = MutableStateFlow(true)
    val showNotifications = _showNotifications.asStateFlow()

    private val _treatTransfersAsIncomeExpense = MutableStateFlow(false)
    val treatTransfersAsIncomeExpense = _treatTransfersAsIncomeExpense.asStateFlow()

    private val _progressState = MutableStateFlow(false)
    val progressState = _progressState.asStateFlow()

    private val _startDateOfMonth = MutableLiveData<Int>()
    val startDateOfMonth = _startDateOfMonth

    private val _opFetchtrns = MutableStateFlow<OpResult<Unit>?>(null)
    val opFetchTrns = _opFetchtrns.asStateFlow()

    fun start() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            val settings = ioThread {
                settingsDao.findFirst()
            }

            _nameLocalAccount.value = settings.name

            _startDateOfMonth.value = startDayOfMonthAct(Unit)!!

            _user.value = null
            _currencyCode.value = settings.currency

            _currentTheme.value = settingsAct(Unit).theme

            _lockApp.value = sharedPrefs.getBoolean(SharedPrefs.APP_LOCK_ENABLED, false)
            _hideCurrentBalance.value =
                sharedPrefs.getBoolean(SharedPrefs.HIDE_CURRENT_BALANCE, false)

            _showNotifications.value = sharedPrefs.getBoolean(SharedPrefs.SHOW_NOTIFICATIONS, true)

            _treatTransfersAsIncomeExpense.value =
                sharedPrefs.getBoolean(SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE, false)

            TestIdlingResource.decrement()
        }
    }

    fun sync() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            _opSync.value = OpResult.loading()

            TestIdlingResource.decrement()
        }
    }

    fun setName(newName: String) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            ioThread {
                settingsDao.save(
                    settingsDao.findFirst().copy(
                        name = newName
                    )
                )
            }
            start()

            TestIdlingResource.decrement()
        }
    }

    fun setCurrency(newCurrency: String) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            ioThread {
                settingsDao.save(
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

    fun exportToCSV(context: Context) {
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

                (context as RootActivity).shareCSVFile(
                    fileUri = fileUri
                )

                TestIdlingResource.decrement()
            }
        }
    }

    fun exportToZip(context: Context) {
        ivyContext.createNewFile(
            "Ivy Wallet (${
                timeNowUTC().formatNicelyWithTime(noWeekDay = true)
            }).zip"
        ) { fileUri ->
            viewModelScope.launch(Dispatchers.IO) {
                TestIdlingResource.increment()

                _progressState.value = true
                backupLogic.exportToFile(zipFileUri = fileUri)
                _progressState.value = false

                sharedPrefs.putBoolean(SharedPrefs.DATA_BACKUP_COMPLETED, true)
                ivyContext.dataBackupCompleted = true

                uiThread {
                    (context as RootActivity).shareZipFile(
                        fileUri = fileUri
                    )
                }

                TestIdlingResource.decrement()
            }
        }
    }

    fun setStartDateOfMonth(startDate: Int) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            when (val res = updateStartDayOfMonthAct(startDate)) {
                is Res.Err -> {}
                is Res.Ok -> {
                    _startDateOfMonth.value = res.data!!
                }
            }

            TestIdlingResource.decrement()
        }
    }

    fun logout() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            logoutLogic.logout()

            TestIdlingResource.decrement()
        }
    }

    fun cloudLogout() {
        viewModelScope.launch {
            TestIdlingResource.increment()

            logoutLogic.cloudLogout()

            TestIdlingResource.decrement()
        }
    }

    fun login() {
        ivyContext.googleSignIn { idToken ->
            if (idToken != null) {
                viewModelScope.launch {
                    TestIdlingResource.increment()

                    try {
                    } catch (e: Exception) {
                        e.sendToCrashlytics(
                            "Settings - GOOGLE_SIGN_IN ERROR: generic exception when logging with GOOGLE"
                        )
                        e.printStackTrace()
                        Timber.e("Settings - Login with Google failed on Ivy server - ${e.message}")
                    }

                    TestIdlingResource.decrement()
                }
            } else {
                sendToCrashlytics("Settings - GOOGLE_SIGN_IN ERROR: idToken is null!!")
                Timber.e("Settings - Login with Google failed while getting idToken")
            }
        }
    }

    fun switchTheme() {
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
            _currentTheme.value = newTheme
        }
    }

    fun setLockApp(lockApp: Boolean) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            sharedPrefs.putBoolean(SharedPrefs.APP_LOCK_ENABLED, lockApp)
            _lockApp.value = lockApp
            refreshWidget(WalletBalanceWidgetReceiver::class.java)

            TestIdlingResource.decrement()
        }
    }

    fun setShowNotifications(showNotifications: Boolean) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            sharedPrefs.putBoolean(SharedPrefs.SHOW_NOTIFICATIONS, showNotifications)
            _showNotifications.value = showNotifications

            TestIdlingResource.decrement()
        }
    }

    fun setHideCurrentBalance(hideCurrentBalance: Boolean) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            sharedPrefs.putBoolean(SharedPrefs.HIDE_CURRENT_BALANCE, hideCurrentBalance)
            _hideCurrentBalance.value = hideCurrentBalance

            TestIdlingResource.decrement()
        }
    }

    fun setTransfersAsIncomeExpense(treatTransfersAsIncomeExpense: Boolean) {
        viewModelScope.launch {
            TestIdlingResource.increment()

            sharedPrefs.putBoolean(
                SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE,
                treatTransfersAsIncomeExpense
            )
            _treatTransfersAsIncomeExpense.value = treatTransfersAsIncomeExpense

            TestIdlingResource.decrement()
        }
    }

    fun deleteAllUserData() {
        viewModelScope.launch {
            logout()
        }
    }

    fun deleteCloudUserData() {
        viewModelScope.launch {
            cloudLogout()
        }
    }
}
