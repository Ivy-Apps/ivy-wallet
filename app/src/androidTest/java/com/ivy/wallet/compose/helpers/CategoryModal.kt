package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.ivy.wallet.compose.IvyComposeTestRule

class CategoryModal(
    private val composeTestRule: IvyComposeTestRule
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