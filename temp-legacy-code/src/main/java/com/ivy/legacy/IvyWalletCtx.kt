package com.ivy.legacy

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ivy.legacy.data.model.MainTab
import com.ivy.legacy.data.model.PaywallReason
import com.ivy.legacy.data.model.TimePeriod
import com.ivy.design.IvyContext
import com.ivy.frp.view.navigation.Navigation
import com.ivy.core.data.model.Account
import com.ivy.core.data.model.Category
import com.ivy.core.data.SharedPrefs
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class IvyWalletCtx : IvyContext() {
    // ------------------------------------------ State ---------------------------------------------
    var startDayOfMonth = 1
        private set

    fun setStartDayOfMonth(day: Int) {
        startDayOfMonth = day
    }

    // ---------------------- Optimization  ----------------------------
    val categoryMap: MutableMap<UUID, Category> = mutableMapOf()
    val accountMap: MutableMap<UUID, Account> = mutableMapOf()
    // ---------------------- Optimization  ----------------------------

    var dataBackupCompleted = false

    @Deprecated("use StartDayOfMonthAct")
    fun initStartDayOfMonthInMemory(sharedPrefs: SharedPrefs): Int {
        startDayOfMonth = sharedPrefs.getInt(SharedPrefs.START_DATE_OF_MONTH, 1)
        return startDayOfMonth
    }

    var selectedPeriod: com.ivy.legacy.data.model.TimePeriod = com.ivy.legacy.data.model.TimePeriod.currentMonth(
        startDayOfMonth = startDayOfMonth // this is default value
    )
    private var selectedPeriodInitialized = false
    fun initSelectedPeriodInMemory(
        startDayOfMonth: Int,
        forceReinitialize: Boolean = false
    ): com.ivy.legacy.data.model.TimePeriod {
        if (!selectedPeriodInitialized || forceReinitialize) {
            selectedPeriod = com.ivy.legacy.data.model.TimePeriod.currentMonth(
                startDayOfMonth = startDayOfMonth
            )
            selectedPeriodInitialized = true
        }

        return selectedPeriod
    }

    fun updateSelectedPeriodInMemory(period: com.ivy.legacy.data.model.TimePeriod) {
        selectedPeriod = period
    }

    var transactionsListState: LazyListState? = null

    var mainTab by mutableStateOf(com.ivy.legacy.data.model.MainTab.HOME)
        private set

    fun selectMainTab(tab: com.ivy.legacy.data.model.MainTab) {
        mainTab = tab
    }

    var moreMenuExpanded = false
        private set

    fun setMoreMenuExpanded(expanded: Boolean) {
        moreMenuExpanded = expanded
    }
    // ------------------------------------------ State ---------------------------------------------

    // ------------------------------------------- Navigation ----------------------------------------
    fun protectWithPaywall(
        paywallReason: com.ivy.legacy.data.model.PaywallReason,
        navigation: Navigation,
        action: () -> Unit
    ) {
        action()
    }
    // ------------------------------------------- Navigation ----------------------------------------

    // Activity help -------------------------------------------------------------------------------
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
    // Activity help -------------------------------------------------------------------------------

    // Billing -------------------------------------------------------------------------------------
    var isPremium = true // if (BuildConfig.DEBUG) Constants.PREMIUM_INITIAL_VALUE_DEBUG else false
    // Billing -------------------------------------------------------------------------------------

    lateinit var googleSignIn: (idTokenResult: (String?) -> Unit) -> Unit

    lateinit var createNewFile: (fileName: String, onCreated: (Uri) -> Unit) -> Unit

    lateinit var openFile: (onOpened: (Uri) -> Unit) -> Unit

    // Testing --------------------------------------------------------------------------------------
    fun reset() {
        mainTab = com.ivy.legacy.data.model.MainTab.HOME
        startDayOfMonth = 1
        isPremium = true
        transactionsListState = null
    }
}
