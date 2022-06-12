package com.ivy.wallet.compose.component

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ivy.wallet.compose.IvyComposeTestRule

class DeleteConfirmationModal(
    private val composeTestRule: IvyComposeTestRule
) {

    fun <T> confirmDelete(next: T): T {
        composeTestRule.onNodeWithText("Delete")
            .performClick()
        return next
    }
}

interface DeleteItem<T> {
    fun deleteWithConfirmation(): T
}