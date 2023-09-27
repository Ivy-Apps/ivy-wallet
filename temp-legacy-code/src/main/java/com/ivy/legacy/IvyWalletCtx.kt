package com.ivy.legacy

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.design.IvyContext
import com.ivy.legacy.data.SharedPrefs
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Deprecated("Legacy code. Don't use it, please.")
@Singleton
class IvyWalletCtx @Inject constructor() : IvyContext() {
    // ------------------------------------------ State ---------------------------------------------
    @Deprecated("Legacy code. Don't use it, please.")
    var startDayOfMonth = 1
        private set

    @Deprecated("Legacy code. Don't use it, please.")
    fun setStartDayOfMonth(day: Int) {
        startDayOfMonth = day
    }

    // ---------------------- Optimization  ----------------------------
    @Deprecated("Legacy code. Don't use it, please.")
    val categoryMap: MutableMap<UUID, Category> = mutableMapOf()

    @Deprecated("Legacy code. Don't use it, please.")
    val accountMap: MutableMap<UUID, Account> = mutableMapOf()
    // ---------------------- Optimization  ----------------------------

    @Deprecated("Legacy code. Don't use it, please.")
    var dataBackupCompleted = false

    @Deprecated("Legacy code. Don't use it, please.")
    fun initStartDayOfMonthInMemory(sharedPrefs: SharedPrefs): Int {
        startDayOfMonth = sharedPrefs.getInt(SharedPrefs.START_DATE_OF_MONTH, 1)
        return startDayOfMonth
    }

    @Deprecated("Legacy code. Don't use it, please.")
    var selectedPeriod: com.ivy.legacy.data.model.TimePeriod =
        com.ivy.legacy.data.model.TimePeriod.currentMonth(
            startDayOfMonth = startDayOfMonth // this is default value
        )

    @Deprecated("Legacy code. Don't use it, please.")
    private var selectedPeriodInitialized = false

    @Deprecated("Legacy code. Don't use it, please.")
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

    @Deprecated("Legacy code. Don't use it, please.")
    fun updateSelectedPeriodInMemory(period: com.ivy.legacy.data.model.TimePeriod) {
        selectedPeriod = period
    }

    @Deprecated("Legacy code. Don't use it, please.")
    var transactionsListState: LazyListState? = null

    @Deprecated("Legacy code. Don't use it, please.")
    var mainTab by mutableStateOf(com.ivy.legacy.data.model.MainTab.HOME)
        private set

    @Deprecated("Legacy code. Don't use it, please.")
    fun selectMainTab(tab: com.ivy.legacy.data.model.MainTab) {
        mainTab = tab
    }

    @Deprecated("Legacy code. Don't use it, please.")
    var moreMenuExpanded = false
        private set

    @Deprecated("Legacy code. Don't use it, please.")
    fun setMoreMenuExpanded(expanded: Boolean) {
        moreMenuExpanded = expanded
    }
    // ------------------------------------------ State ---------------------------------------------

    // Activity help -------------------------------------------------------------------------------
    @Deprecated("Legacy code. Don't use it, please.")
    lateinit var onShowDatePicker: (
        minDate: LocalDate?,
        maxDate: LocalDate?,
        initialDate: LocalDate?,
        onDatePicked: (LocalDate) -> Unit
    ) -> Unit
    lateinit var onShowTimePicker: (onDatePicked: (LocalTime) -> Unit) -> Unit

    @Deprecated("Legacy code. Don't use it, please.")
    fun datePicker(
        minDate: LocalDate? = null,
        maxDate: LocalDate? = null,
        initialDate: LocalDate?,
        onDatePicked: (LocalDate) -> Unit
    ) {
        onShowDatePicker(minDate, maxDate, initialDate, onDatePicked)
    }

    @Deprecated("Legacy code. Don't use it, please.")
    fun timePicker(onTimePicked: (LocalTime) -> Unit) {
        onShowTimePicker(onTimePicked)
    }
    // Activity help -------------------------------------------------------------------------------

    // Billing -------------------------------------------------------------------------------------
    @Deprecated("Legacy code. Don't use it, please.")
    var isPremium = true // if (BuildConfig.DEBUG) Constants.PREMIUM_INITIAL_VALUE_DEBUG else false
    // Billing -------------------------------------------------------------------------------------

    @Deprecated("Legacy code. Don't use it, please.")
    lateinit var googleSignIn: (idTokenResult: (String?) -> Unit) -> Unit

    @Deprecated("Legacy code. Don't use it, please.")
    lateinit var createNewFile: (fileName: String, onCreated: (Uri) -> Unit) -> Unit

    @Deprecated("Legacy code. Don't use it, please.")
    lateinit var openFile: (onOpened: (Uri) -> Unit) -> Unit

    // Testing --------------------------------------------------------------------------------------
    @Deprecated("Legacy code. Don't use it, please.")
    fun reset() {
        mainTab = com.ivy.legacy.data.model.MainTab.HOME
        startDayOfMonth = 1
        isPremium = true
        transactionsListState = null
    }
}
