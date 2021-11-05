package com.ivy.wallet.ui.settings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.analytics.IvyAnalytics
import com.ivy.wallet.base.*
import com.ivy.wallet.logic.LogoutLogic
import com.ivy.wallet.logic.csv.ExportCSVLogic
import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.model.analytics.AnalyticsEvent
import com.ivy.wallet.model.entity.User
import com.ivy.wallet.network.FCMClient
import com.ivy.wallet.network.RestClient
import com.ivy.wallet.network.request.auth.GoogleSignInRequest
import com.ivy.wallet.network.request.github.OpenIssueRequest
import com.ivy.wallet.network.service.GithubService
import com.ivy.wallet.persistence.SharedPrefs
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.persistence.dao.UserDao
import com.ivy.wallet.session.IvySession
import com.ivy.wallet.sync.IvySync
import com.ivy.wallet.ui.IvyActivity
import com.ivy.wallet.ui.IvyContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDao: SettingsDao,
    private val ivySession: IvySession,
    private val userDao: UserDao,
    private val ivyContext: IvyContext,
    private val ivySync: IvySync,
    private val exportCSVLogic: ExportCSVLogic,
    private val restClient: RestClient,
    private val fcmClient: FCMClient,
    private val ivyAnalytics: IvyAnalytics,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val logoutLogic: LogoutLogic,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user = _user.asLiveData()

    private val _nameLocalAccount = MutableLiveData<String?>()
    val nameLocalAccount = _nameLocalAccount.asLiveData()

    private val _opSync = MutableLiveData<OpResult<Boolean>>()
    val opSync = _opSync.asLiveData()

    private val _currencyCode = MutableLiveData<String>()
    val currencyCode = _currencyCode.asLiveData()

    private val _lockApp = MutableLiveData<Boolean>()
    val lockApp = _lockApp.asLiveData()

    private val _startDateOfMonth = MutableLiveData<Int>()
    val startDateOfMonth = _startDateOfMonth

    fun start() {
        viewModelScope.launch {
            val settings = ioThread { settingsDao.findFirst() }

            _nameLocalAccount.value = settings.name

            ivyContext.startDateOfMonth = sharedPrefs.getInt(SharedPrefs.START_DATE_OF_MONTH, 1)
            _startDateOfMonth.value = ivyContext.startDateOfMonth

            _user.value = ioThread {
                val userId = ivySession.getUserIdSafe()
                if (userId != null) userDao.findById(userId) else null
            }
            _currencyCode.value = settings.currency

            _lockApp.value = sharedPrefs.getBoolean(SharedPrefs.LOCK_APP, false)

            _opSync.value = OpResult.success(ioThread { ivySync.isSynced() })
        }
    }

    fun sync() {
        viewModelScope.launch {
            _opSync.value = OpResult.loading()

            ioThread {
                ivySync.sync()
            }

            _opSync.value = OpResult.success(ioThread { ivySync.isSynced() })
        }
    }


    fun setName(newName: String) {
        viewModelScope.launch {
            ioThread {
                settingsDao.save(
                    settingsDao.findFirst().copy(
                        name = newName
                    )
                )
            }
            start()
        }
    }

    fun setCurrency(newCurrency: String) {
        viewModelScope.launch {
            ioThread {
                settingsDao.save(
                    settingsDao.findFirst().copy(
                        currency = newCurrency
                    )
                )

                exchangeRatesLogic.sync(baseCurrency = newCurrency)
            }
            start()
        }
    }

    fun exportToCSV(context: Context) {
        ivyContext.createNewFile(
            "Ivy Wallet (${
                timeNowUTC().formatNicelyWithTime(noWeekDay = true)
            }).csv"
        ) { fileUri ->
            viewModelScope.launch {
                exportCSVLogic.exportToFile(
                    context = context,
                    fileUri = fileUri
                )

                (context as IvyActivity).shareCSVFile(
                    fileUri = fileUri
                )
            }
        }
    }

    fun setStartDateOfMonth(startDate: Int) {
        if (startDate in 1..31) {
            sharedPrefs.putInt(SharedPrefs.START_DATE_OF_MONTH, startDate)
            ivyContext.startDateOfMonth = startDate
            _startDateOfMonth.value = startDate
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutLogic.logout()
        }
    }

    fun login() {
        ivyContext.googleSignIn { idToken ->
            if (idToken != null) {
                viewModelScope.launch {
                    try {
                        val authResponse = restClient.authService.googleSignIn(
                            GoogleSignInRequest(
                                googleIdToken = idToken,
                                fcmToken = fcmClient.fcmToken()
                            )
                        )

                        ioThread {
                            ivySession.initiate(authResponse)

                            settingsDao.save(
                                settingsDao.findFirst().copy(
                                    name = authResponse.user.firstName
                                )
                            )
                        }

                        start()

                        ioThread {
                            ivyAnalytics.logEvent(AnalyticsEvent.LOGIN_FROM_SETTINGS)
                        }

                        sync()
                    } catch (e: Exception) {
                        e.sendToCrashlytics("Settings - GOOGLE_SIGN_IN ERROR: generic exception when logging with GOOGLE")
                        e.printStackTrace()
                        Timber.e("Settings - Login with Google failed on Ivy server - ${e.message}")
                    }
                }
            } else {
                sendToCrashlytics("Settings - GOOGLE_SIGN_IN ERROR: idToken is null!!")
                Timber.e("Settings - Login with Google failed while getting idToken")
            }
        }
    }

    fun setLockApp(lockApp: Boolean) {
        viewModelScope.launch {
            sharedPrefs.putBoolean(SharedPrefs.LOCK_APP, lockApp)
            _lockApp.value = lockApp
        }
    }

    fun requestFeature(
        ivyActivity: IvyActivity,
        title: String,
        body: String
    ) {
        viewModelScope.launch {
            try {
                val response = restClient.githubService.openIssue(
                    request = OpenIssueRequest(
                        title = title,
                        body = body,
                        labels = listOf(
                            GithubService.LABEL_USER_REQUEST
                        )
                    )
                )

                ivyActivity.openUrlInBrowser(response.url)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}