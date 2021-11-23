package com.ivy.wallet.compose.scenario

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.ivy.wallet.base.timeNowUTC
import com.ivy.wallet.compose.IvyComposeTest
import com.ivy.wallet.compose.helpers.EditPlannedScreen
import com.ivy.wallet.compose.helpers.HomeTab
import com.ivy.wallet.compose.helpers.OnboardingFlow
import com.ivy.wallet.compose.helpers.TransactionFlow
import com.ivy.wallet.model.IntervalType
import com.ivy.wallet.model.TransactionType
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class PlannedPaymentsTest : IvyComposeTest() {

    private val onboardingFlow = OnboardingFlow(composeTestRule)
    private val transactionFlow = TransactionFlow(composeTestRule)
    private val editPlannedScreen = EditPlannedScreen(composeTestRule)
    private val homeTab = HomeTab(composeTestRule)

    @Test
    fun Onboard_CreatePlannedPaymentFromPrompt() {
        onboardingFlow.quickOnboarding()
        transactionFlow.addIncome(
            amount = 100.0
        )

        composeTestRule.onNodeWithTag("cta_prompt_add_planned_payment")
            .performClick()

        editPlannedScreen.addPlannedPayment(
            type = TransactionType.INCOME,
            oneTime = false,
            amount = "2,000",
            category = "Bills & Fees",
            startDate = timeNowUTC().withDayOfMonth(1),
            intervalN = 3,
            intervalType = IntervalType.WEEK,
            title = "Salary"
        )

        homeTab.assertUpcomingIncome(
            amount = "2,000.00",
            currency = "USD"
        )

        homeTab.clickUpcoming()

        homeTab.clickTransaction(
            amount = "2,000.00",
            category = "Bills & Fees",
            title = "Salary"
        )

        editPlannedScreen.clickGet()

        homeTab.assertBalance(
            amount = "2,100",
            amountDecimal = ".00",
            currency = "USD"
        )
    }
}