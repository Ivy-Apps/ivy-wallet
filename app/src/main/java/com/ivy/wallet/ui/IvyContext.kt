package com.ivy.wallet.ui

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ivy.wallet.BuildConfig
import com.ivy.wallet.Constants
import com.ivy.wallet.persistence.SharedPrefs
import com.ivy.wallet.ui.main.MainTab
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import com.ivy.wallet.ui.paywall.PaywallReason
import com.ivy.wallet.ui.theme.Theme
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class IvyContext {
    var currentScreen: Screen? by mutableStateOf(null)
        private set

    var theme: Theme by mutableStateOf(Theme.LIGHT)
        private set

    var screenWidth: Int = -1
        get() {
            return if (field > 0) field else throw IllegalStateException("screenWidth not initialized")
        }
    var screenHeight: Int = -1
        get() {
            return if (field > 0) field else throw IllegalStateException("screenHeight not initialized")
        }

    //------------------------------------------ State ---------------------------------------------
    var startDayOfMonth = 1
    fun initStartDayOfMonthInMemory(sharedPrefs: SharedPrefs): Int {
        startDayOfMonth = sharedPrefs.getInt(SharedPrefs.START_DATE_OF_MONTH, 1)
        return startDayOfMonth
    }

    fun updateStartDayOfMonthWithPersistence(
        sharedPrefs: SharedPrefs,
        startDayOfMonth: Int
    ) {
        sharedPrefs.putInt(SharedPrefs.START_DATE_OF_MONTH, startDayOfMonth)
        this.startDayOfMonth = startDayOfMonth

        //when start day of month the selected time period must be reinitialized
        initSelectedPeriodInMemory(
            startDayOfMonth = startDayOfMonth,
            forceReinitialize = true
        )
    }

    var selectedPeriod: TimePeriod = TimePeriod.currentMonth(
        startDayOfMonth = startDayOfMonth //this is default value
    )
    private var selectedPeriodInitialized = false
    fun initSelectedPeriodInMemory(
        startDayOfMonth: Int,
        forceReinitialize: Boolean = false
    ): TimePeriod {
        if (!selectedPeriodInitialized || forceReinitialize) {
            selectedPeriod = TimePeriod.currentMonth(
                startDayOfMonth = startDayOfMonth
            )
            selectedPeriodInitialized = true
        }

        return selectedPeriod
    }

    fun updateSelectedPeriodInMemory(period: TimePeriod) {
        selectedPeriod = period
    }


    var transactionsListState: LazyListState? = null

    var mainTab by mutableStateOf(MainTab.HOME)
        private set

    fun selectMainTab(tab: MainTab) {
        mainTab = tab
    }

    var moreMenuExpanded = false
        private set

    fun setMoreMenuExpanded(expanded: Boolean) {
        moreMenuExpanded = expanded
    }
    //------------------------------------------ State ---------------------------------------------

    //------------------------------------------- BackStack ----------------------------------------
    private val backStack: Stack<Screen> = Stack()

    private var lastScreen: Screen? = null

    var modalBackHandling: Stack<ModalBackHandler> = Stack()

    data class ModalBackHandler(
        val id: UUID,
        val onBackPressed: () -> Boolean
    )

    fun protectWithPaywall(paywallReason: PaywallReason, action: () -> Unit) {
        if (isPremium || (BuildConfig.DEBUG && !Constants.ENABLE_PAYWALL_ON_DEBUG)) {
            action()
        } else {
            navigateTo(
                Screen.Paywall(
                    paywallReason = paywallReason
                )
            )
        }
    }

    fun lastModalBackHandlerId(): UUID? {
        return if (modalBackHandling.isEmpty()) {
            null
        } else {
            modalBackHandling.peek().id
        }
    }

    var onBackPressed: MutableMap<Screen, () -> Boolean> = mutableMapOf()


    fun navigateTo(screen: Screen, allowBackStackStore: Boolean = true) {
        if (lastScreen != null && allowBackStackStore) {
            backStack.push(lastScreen)
        }
        switchScreen(screen, allowBackStackStore)
    }

    fun resetBackStack() {
        while (!backStackEmpty()) {
            popBackStack()
        }
        lastScreen = null
    }

    fun backStackEmpty() = backStack.empty()

    fun popBackStackSafe() {
        if (!backStackEmpty()) {
            popBackStack()
        }
    }

    private fun popBackStack() {
        backStack.pop()
    }

    fun onBackPressed(): Boolean {
        if (modalBackHandling.isNotEmpty()) {
            return modalBackHandling.peek().onBackPressed()
        }
        val specialHandling = onBackPressed.getOrDefault(currentScreen, { false }).invoke()
        return specialHandling || back()
    }

    fun back(): Boolean {
        if (!backStack.empty()) {
            switchScreen(backStack.pop())
            return true
        }
        return false
    }

    fun lastBackstackScreen(): Screen? {
        return if (!backStackEmpty()) {
            backStack.peek()
        } else {
            null
        }
    }

    private fun switchScreen(screen: Screen, allowBackStackStore: Boolean = true) {
        this.currentScreen = screen
        if (allowBackStackStore)
            lastScreen = screen
    }
    //------------------------------------------- BackStack ----------------------------------------

    //Activity help -------------------------------------------------------------------------------
    lateinit var onShowDatePicker: (
        minDate: LocalDate?,
        maxDate: LocalDate?,
        initialDate: LocalDate?,
        onDatePicked: (LocalDate) -> Unit
    ) -> Unit
    lateinit var onShowTimePicker: (onDatePicked: (LocalTime) -> Unit) -> Unit

    fun datePicker(
        minDate: LocalDate? = null,
        maxDate: LocalDate? = null,
        initialDate: LocalDate?,
        onDatePicked: (LocalDate) -> Unit
    ) {
        onShowDatePicker(minDate, maxDate, initialDate, onDatePicked)
    }

    fun timePicker(onTimePicked: (LocalTime) -> Unit) {
        onShowTimePicker(onTimePicked)
    }
    //Activity help -------------------------------------------------------------------------------


    // Billing -------------------------------------------------------------------------------------
    var isPremium = if (BuildConfig.DEBUG) Constants.PREMIUM_INITIAL_VALUE_DEBUG else false
    // Billing -------------------------------------------------------------------------------------

    lateinit var googleSignIn: (idTokenResult: (String?) -> Unit) -> Unit

    lateinit var createNewFile: (fileName: String, onCreated: (Uri) -> Unit) -> Unit

    lateinit var openFile: (onOpened: (Uri) -> Unit) -> Unit

    fun switchTheme(theme: Theme) {
        this.theme = theme
    }

    //Testing --------------------------------------------------------------------------------------
    fun reset() {
        mainTab = MainTab.HOME
        startDayOfMonth = 1
        currentScreen = null
        isPremium = false
        transactionsListState = null
        resetBackStack()
    }
}
