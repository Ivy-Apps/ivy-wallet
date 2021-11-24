package com.ivy.wallet.compose.helpers

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.rules.ActivityScenarioRule

class CategoryModal<A : ComponentActivity>(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>
) {
    val colorPicker = IvyColorPicker(composeTestRule)
    val chooseIconFlow = ChooseIconFlow(composeTestRule)

    fun enterTitle(
        title: String
    ) {
        composeTestRule.onNodeWithTag("base_input")
            .performTextReplacement(title)
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
}