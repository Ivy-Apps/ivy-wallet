package com.ivy.core.ui.temp

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ivy.base.MainTab
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import com.ivy.design.IvyContext
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class IvyWalletCtx : IvyContext() {
    //------------------------------------------ State ---------------------------------------------
    var startDayOfMonth = 1
        private set

    fun setStartDayOfMonth(day: Int) {
        startDayOfMonth = day
    }

    //---------------------- Optimization  ----------------------------
    @Deprecated("use IvyState")
    val categoryMap: MutableMap<UUID, CategoryOld> = mutableMapOf()

    @Deprecated("use IvyState")
    val accountMap: MutableMap<UUID, AccountOld> = mutableMapOf()
    //---------------------- Optimization  ----------------------------

    @Deprecated("use IvyState")
    var selectedPeriod: TimePeriod = TimePeriod.currentMonth(
        startDayOfMonth = startDayOfMonth //this is default value
    )
    private var selectedPeriodInitialized = false

    @Deprecated("use IvyState")
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

    @Deprecated("use IvyState")
    fun updateSelectedPeriodInMemory(period: TimePeriod) {
        selectedPeriod = period
    }

    @Deprecated("use IvyState")
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
    var isPremium = true //if (BuildConfig.DEBUG) Constants.PREMIUM_INITIAL_VALUE_DEBUG else false
    // Billing -------------------------------------------------------------------------------------

    lateinit var googleSignIn: (idTokenResult: (String?) -> Unit) -> Unit

    lateinit var createNewFile: (fileName: String, onCreated: (Uri) -> Unit) -> Unit

    lateinit var openFile: (onOpened: (Uri) -> Unit) -> Unit

    //Testing --------------------------------------------------------------------------------------
    fun reset() {
        mainTab = MainTab.HOME
        startDayOfMonth = 1
        isPremium = true
        transactionsListState = null
    }
}
