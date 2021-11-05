package com.ivy.wallet.ui

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ivy.wallet.BuildConfig
import com.ivy.wallet.Constants
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
    var selectedPeriod: TimePeriod = TimePeriod.thisMonth() //original state
    var transactionsListState: LazyListState? = null

    var startDateOfMonth = 1

    var mainTab by mutableStateOf(MainTab.HOME)
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


    fun navigateTo(screen: Screen) {
        if (lastScreen != null) {
            backStack.push(lastScreen)
        }
        switchScreen(screen)
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

    private fun switchScreen(screen: Screen) {
        this.currentScreen = screen
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
    lateinit var onContactSupport: () -> Unit

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

    fun contactSupport() {
        onContactSupport()
    }
    //Activity help -------------------------------------------------------------------------------


    // Billing -------------------------------------------------------------------------------------
    var isPremium = false
    // Billing -------------------------------------------------------------------------------------

    lateinit var googleSignIn: (idTokenResult: (String?) -> Unit) -> Unit

    lateinit var createNewFile: (fileName: String, onCreated: (Uri) -> Unit) -> Unit

    lateinit var openFile: (onOpened: (Uri) -> Unit) -> Unit

    fun switchTheme(theme: Theme) {
        this.theme = theme
    }
}
