package com.ivy.wallet.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor() : ViewModel() {

//    companion object {
//        const val EXTRA_ADD_TRANSACTION_TYPE = "add_transaction_type_extra"
//    }
//
//    private var appLockEnabled = false
//
//    private val _appLocked = MutableStateFlow<Boolean?>(null)
//    val appLocked = _appLocked.readOnly()
//
//
//    fun start(systemDarkMode: Boolean, intent: Intent) {
//        viewModelScope.launch {
//            TestIdlingResource.increment()
//
//            ioThread {
//                val theme = settingsDao.findAll().firstOrNull()?.theme
//                    ?: if (systemDarkMode) Theme.DARK else Theme.LIGHT
//                ivyContext.switchTheme(theme)
//
//                startDayOfMonthAct(Unit)
//            }
//
//            TestIdlingResource.decrement()
//        }
//
//        viewModelScope.launch {
//            TestIdlingResource.increment()
//
//            ioThread {
//                ivySession.loadFromCache()
//
//                appLockEnabled = sharedPrefs.getBoolean(SharedPrefs.APP_LOCK_ENABLED, false)
//                //initial app locked state
//                _appLocked.value = appLockEnabled
//
//                if (isOnboardingCompleted()) {
//                    navigateOnboardedUser(intent)
//                } else {
////                    nav.navigateTo(Onboarding)
//                }
//
//            }
//
//            TestIdlingResource.decrement()
//        }
//    }
//
//    private fun navigateOnboardedUser(intent: Intent) {
//        if (!handleSpecialStart(intent)) {
////            nav.navigateTo(Main)
//            transactionReminderLogic.scheduleReminder()
//        }
//    }
//
//    private fun handleSpecialStart(intent: Intent): Boolean {
//        val addTrnType: TrnTypeOld? = try {
//            intent.getSerializableExtra(EXTRA_ADD_TRANSACTION_TYPE) as? TrnTypeOld
//                ?: TrnTypeOld.valueOf(intent.getStringExtra(EXTRA_ADD_TRANSACTION_TYPE) ?: "")
//        } catch (e: IllegalArgumentException) {
//            null
//        }
//
//
//        if (addTrnType != null) {
////            nav.navigateTo(
////                EditTransaction(
////                    initialTransactionId = null,
////                    type = addTrnType
////                )
////            )
//
//            return true
//        }
//
//        return false
//    }
//
//    fun handleBiometricAuthResult(
//        onAuthSuccess: () -> Unit = {}
//    ): BiometricPrompt.AuthenticationCallback {
//        return object : BiometricPrompt.AuthenticationCallback() {
//            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
//                Timber.d(com.ivy.core.ui.temp.stringRes(R.string.authentication_succeeded))
//                unlockApp()
//                onAuthSuccess()
//            }
//
//            override fun onAuthenticationFailed() {
//                Timber.d(com.ivy.core.ui.temp.stringRes(R.string.authentication_failed))
//            }
//
//            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
//
//            }
//        }
//    }
//
//    fun initBilling(activity: AppCompatActivity) {
//        ivyBilling.init(
//            activity = activity,
//            onReady = {
//                viewModelScope.launch {
//                    val purchases = ivyBilling.queryPurchases()
////                    paywallLogic.processPurchases(purchases)
//                }
//            },
//            onPurchases = { purchases ->
//                viewModelScope.launch {
////                    paywallLogic.processPurchases(purchases)
//                }
//
//            },
//            onError = { code, msg ->
//                Timber.e("Billing error code=$code: $msg")
//            }
//        )
//    }
//
//    private fun isOnboardingCompleted(): Boolean {
//        return sharedPrefs.getBoolean(SharedPrefs.ONBOARDING_COMPLETED, false)
//    }
//
//
//    //App Lock & UserInactivity --------------------------------------------------------------------
//    fun isAppLockEnabled(): Boolean {
//        return appLockEnabled
//    }
//
//    fun isAppLocked(): Boolean {
//        //by default we assume that the app is locked
//        return appLocked.value ?: true
//    }
//
//    fun lockApp() {
//        _appLocked.value = true
//    }
//
//    fun unlockApp() {
//        _appLocked.value = false
//    }
//
//    private val userInactiveTime = AtomicLong(0)
//    private var userInactiveJob: Job? = null
//
//    fun startUserInactiveTimeCounter() {
//        if (userInactiveJob != null && userInactiveJob!!.isActive) return
//
//        userInactiveJob = viewModelScope.launch(Dispatchers.IO) {
//            while (userInactiveTime.get() < Constants.USER_INACTIVITY_TIME_LIMIT &&
//                userInactiveJob != null && !userInactiveJob?.isCancelled!!
//            ) {
//                delay(1000)
//                userInactiveTime.incrementAndGet()
//            }
//
//            if (!isAppLocked()) {
//                lockApp()
//            }
//
//            cancel()
//        }
//    }
//
//    fun checkUserInactiveTimeStatus() {
//        if (userInactiveTime.get() < Constants.USER_INACTIVITY_TIME_LIMIT) {
//            if (userInactiveJob != null && userInactiveJob?.isCancelled == false) {
//                userInactiveJob?.cancel()
//                resetUserInactiveTimer()
//            }
//        }
//    }
//
//    fun resetUserInactiveTimer() {
//        userInactiveTime.set(0)
//    }
}