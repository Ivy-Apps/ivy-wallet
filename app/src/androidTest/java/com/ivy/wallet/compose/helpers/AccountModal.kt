package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule

class AccountModal<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    val ivyColorPicker = IvyColorPicker(composeTestRule)
    val chooseIconFlow = ChooseIconFlow(composeTestRule)
    val currencyPicker = CurrencyPicker(composeTestRule)

    fun enterTitle(
        title: String
    ) {
        composeTestRule.onNodeWithTag("base_input")
            .performTextReplacement(title)
    }

    fun clickBalance() {
        composeTestRule
            .onNode(hasTestTag("amount_balance"))
            .performClick()
    }

    fun chooseCurrency() {
        composeTestRule.onNodeWithTag("account_modal_currency")
            .performClick()
    }

    fun clickSave() {
        composeTestRule
            .onNode(hasText("Save"))
            .performClick()
    }

    fun clickAdd() {
        composeTestRule
            .onNode(hasText("Add"))
            .performClick()
    }

    fun tapIncludeInBalance() {
        composeTestRule.onNodeWithText("Include in balance")
            .performClick()
    }
}