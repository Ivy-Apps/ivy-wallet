package com.ivy.wallet.compose.scenario.lab

import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.component.edittrn.ChooseCategoryModal
import com.ivy.wallet.compose.component.planned.EditPlannedScreen
import com.ivy.wallet.domain.data.TransactionType
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Ignore
import org.junit.Test

@HiltAndroidTest
class ExperimentalTest : IvyComposeTest() {
    @Ignore("It's an experiment, not a real test")
    @Test
    fun openCalendar() = testDebug {
        quickOnboarding()
            .clickAddFAB()
            .clickAddPlannedPayment()
            .setPaymentType(TransactionType.EXPENSE)
            .enterNumber(number = "10", next = ChooseCategoryModal(composeTestRule))
            .selectCategory("Food & Drinks", next = EditPlannedScreen(composeTestRule))
            .clickRecurringModalPickDate()
            .print()
    }
}