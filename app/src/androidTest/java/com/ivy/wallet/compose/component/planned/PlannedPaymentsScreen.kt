package com.ivy.wallet.compose.component.planned

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.home.HomeMoreMenu

class PlannedPaymentsScreen(
    private val composeTestRule: IvyComposeTestRule
) {

    fun clickAddPlannedPayment(): EditPlannedScreen {
        composeTestRule.onNodeWithText("Add payment")
            .performClick()
        return EditPlannedScreen(composeTestRule)
    }

    fun clickPlannedPayment(
        amount: String
    ): EditPlannedScreen {
        composeTestRule.onNode(
            hasTestTag("planned_payment_card")
                .and(hasAnyDescendant(hasText(amount))),
            useUnmergedTree = true
        )
            .performScrollTo()
            .performClick()

        return EditPlannedScreen(composeTestRule)
    }

    fun assertPlannedPaymentDoesNotExist(
        amount: String
    ): PlannedPaymentsScreen {
        composeTestRule.onNode(
            hasTestTag("planned_payment_card")
                .and(hasAnyDescendant(hasText(amount))),
            useUnmergedTree = true
        ).assertDoesNotExist()

        return this
    }

    fun assertUpcomingExpense(
        amount: String,
        currency: String
    ): PlannedPaymentsScreen {
        composeTestRule.onNodeWithTag(
            testTag = "upcoming_expense",
            useUnmergedTree = true
        ).assertTextEquals("$amount $currency")

        return this
    }

    fun assertUpcomingDoesNotExist(): PlannedPaymentsScreen {
        composeTestRule.onNodeWithTag(
            testTag = "upcoming_title",
            useUnmergedTree = true
        ).assertDoesNotExist()

        return this
    }

    fun clickClose(): HomeMoreMenu {
        composeTestRule.onNodeWithContentDescription("close")
            .performClick()

        return HomeMoreMenu(composeTestRule)
    }
}