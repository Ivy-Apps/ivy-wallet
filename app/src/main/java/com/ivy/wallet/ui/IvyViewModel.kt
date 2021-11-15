package com.ivy.wallet.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.analytics.IvyAnalytics
import com.ivy.wallet.base.asLiveData
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.base.sendToCrashlytics
import com.ivy.wallet.base.uiThread
import com.ivy.wallet.billing.IvyBilling
import com.ivy.wallet.logic.PaywallLogic
import com.ivy.wallet.logic.notification.TransactionReminderLogic
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.persistence.SharedPrefs
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.session.IvySession
import com.ivy.wallet.ui.theme.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class IvyViewModel @Inject constructor(
    private val ivyContext: IvyContext,
    private val ivyAnalytics: IvyAnalytics,
    private val settingsDao: SettingsDao,
    private val sharedPrefs: SharedPrefs,
    private val ivySession: IvySession,
    private val ivyBilling: IvyBilling,
    private val paywallLogic: PaywallLogic,
    private val transactionReminderLogic: TransactionReminderLogic
) : ViewModel() {

    companion object {
        const val EXTRA_ADD_TRANSACTION_TYPE = "add_transaction_type_extra"
    }

    private val _appLockedEnabled = MutableLiveData<Boolean>()
    val appLockedEnabled = _appLockedEnabled.asLiveData()

    fun start(systemDarkMode: Boolean, intent: Intent) {
        viewModelScope.launch {
            ioThread {
                val theme = settingsDao.findAll().firstOrNull()?.theme
                    ?: if (systemDarkMode) Theme.DARK else Theme.LIGHT
                ivyContext.switchTheme(theme)

                ivyContext.initStartDayOfMonthInMemory(sharedPrefs = sharedPrefs)
            }
        }

        viewModelScope.launch {
            ioThread {
                ivySession.loadFromCache()
                ivyAnalytics.loadSession()

                if (onboardingCompleted()) {
                    val appLocked = sharedPrefs.getBoolean(SharedPrefs.LOCK_APP, false)
                    uiThread {
                        _appLockedEnabled.value = appLocked
                    }

                    if (!appLocked) {
                        continueNavigation(intent)
                    }
                } else {
                    ivyContext.navigateTo(Screen.Onboarding)
                }
            }
        }
    }


    private fun handleSpecialStart(intent: Intent): Boolean {
        val addTrnType = intent.getSerializableExtra(EXTRA_ADD_TRANSACTION_TYPE) as? TransactionType
        if (addTrnType != null) {
            ivyContext.navigateTo(
                Screen.EditTransaction(
                    initialTransactionId = null,
                    type = addTrnType
                )
            )

            return true
        }

        return false
    }

    fun handleBiometricAuthenticationResult(onAuthSuccess: () -> Unit): BiometricPrompt.AuthenticationCallback {
        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                Timber.d("Authentication succeeded!")
                onAuthSuccess()
            }

            override fun onAuthenticationFailed() {
                Timber.d("Authentication failed.")
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {

            }
        }
    }

    fun unlockAuthenticated(intent: Intent) {
        _appLockedEnabled.value = false
        continueNavigation(intent)
    }

    private fun continueNavigation(intent: Intent) {
        if (!handleSpecialStart(intent)) {
            ivyContext.navigateTo(Screen.Main)
            transactionReminderLogic.scheduleReminder()
        }
    }

    fun initBilling(activity: AppCompatActivity) {
        ivyBilling.init(
            activity = activity,
            onReady = {
                viewModelScope.launch {
                    val purchases = ivyBilling.queryPurchases()
                    paywallLogic.processPurchases(purchases)
                }
            },
            onPurchases = { purchases ->
                viewModelScope.launch {
                    paywallLogic.processPurchases(purchases)
                }

            },
            onError = { code, msg ->
                sendToCrashlytics("IvyActivity Billing error: code=$code: $msg")
                Timber.e("Billing error code=$code: $msg")
            }
        )
    }

    private fun onboardingCompleted() =
        sharedPrefs.getBoolean(SharedPrefs.ONBOARDING_COMPLETED, false)
}