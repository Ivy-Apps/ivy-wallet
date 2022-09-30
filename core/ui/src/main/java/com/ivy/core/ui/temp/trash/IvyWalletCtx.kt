package com.ivy.core.ui.temp.trash

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ivy.base.MainTab
import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import com.ivy.design.IvyContext
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

@Deprecated("don't use, it's bad!")
class IvyWalletCtx : IvyContext() {
    //------------------------------------------ State ---------------------------------------------
    @Deprecated("don't use, it's bad!")
    var startDayOfMonth = 1
        private set

    @Deprecated("don't use, it's bad!")
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

    @Deprecated("don't use, it's bad!")
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

    @Deprecated("don't use, it's bad!")
    var mainTab by mutableStateOf(MainTab.HOME)
        private set

    @Deprecated("don't use, it's bad!")
    fun selectMainTab(tab: MainTab) {
        mainTab = tab
    }

    @Deprecated("don't use, it's bad!")
    var moreMenuExpanded = false
        private set

    @Deprecated("don't use, it's bad!")
    fun setMoreMenuExpanded(expanded: Boolean) {
        moreMenuExpanded = expanded
    }
    //------------------------------------------ State ---------------------------------------------


    //Activity help -------------------------------------------------------------------------------
    @Deprecated("don't use, it's bad!")
    lateinit var onShowDatePicker: (
        minDate: LocalDate?,
        maxDate: LocalDate?,
        initialDate: LocalDate?,
        onDatePicked: (LocalDate) -> Unit
    ) -> Unit

    @Deprecated("don't use, it's bad!")
    lateinit var onShowTimePicker: (onDatePicked: (LocalTime) -> Unit) -> Unit

    @Deprecated("don't use, it's bad!")
    fun datePicker(
        minDate: LocalDate? = null,
        maxDate: LocalDate? = null,
        initialDate: LocalDate?,
        onDatePicked: (LocalDate) -> Unit
    ) {
        onShowDatePicker(minDate, maxDate, initialDate, onDatePicked)
    }

    @Deprecated("don't use, it's bad!")
    fun timePicker(onTimePicked: (LocalTime) -> Unit) {
        onShowTimePicker(onTimePicked)
    }
    //Activity help -------------------------------------------------------------------------------


    // Billing -------------------------------------------------------------------------------------
    @Deprecated("don't use, it's bad!")
    var isPremium = true //if (BuildConfig.DEBUG) Constants.PREMIUM_INITIAL_VALUE_DEBUG else false
    // Billing -------------------------------------------------------------------------------------

    @Deprecated("don't use, it's bad!")
    lateinit var googleSignIn: (idTokenResult: (String?) -> Unit) -> Unit

    @Deprecated("don't use, it's bad!")
    lateinit var createNewFile: (fileName: String, onCreated: (Uri) -> Unit) -> Unit

    @Deprecated("don't use, it's bad!")
    lateinit var openFile: (onOpened: (Uri) -> Unit) -> Unit

    //Testing --------------------------------------------------------------------------------------
    @Deprecated("don't use, it's bad!")
    fun reset() {
        mainTab = MainTab.HOME
        startDayOfMonth = 1
        isPremium = true
        transactionsListState = null
    }
}
