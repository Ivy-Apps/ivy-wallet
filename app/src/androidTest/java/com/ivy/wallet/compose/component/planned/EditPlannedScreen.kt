package com.ivy.wallet.compose.component.planned

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.compose.component.DeleteConfirmationModal
import com.ivy.wallet.compose.component.amountinput.IvyAmountInput
import com.ivy.wallet.compose.component.edittrn.ChooseCategoryModal
import com.ivy.wallet.compose.component.external.CalendarDialog
import com.ivy.wallet.domain.data.IntervalType
import com.ivy.wallet.domain.data.TransactionType
import java.time.LocalDateTime

class EditPlannedScreen(
    private val composeTestRule: IvyComposeTestRule
) {
    //TODO: Re-work: make UI options strongly typed!

    fun setPaymentType(type: TransactionType): IvyAmountInput {
        val nodeText = when (type) {
            TransactionType.INCOME -> "modal_type_${type.name}"
            TransactionType.EXPENSE -> "modal_type_${type.name}"
            TransactionType.TRANSFER -> error("Unsupported type for planned payments")
        }
        composeTestRule.onNodeWithTag(nodeText)
            .performClick()

        return IvyAmountInput(composeTestRule)
    }

    fun setRecurring(
        oneTime: Boolean,
        startDate: LocalDateTime?,
        intervalN: Int?,
        intervalType: IntervalType?,
    ): EditPlannedScreen {
        if (oneTime) {
            composeTestRule.onNodeWithText("One time")
                .performClick()
        } else {
            composeTestRule.onNodeWithText("Multiple times")
                .performClick()
        }

        //Compose Calendar not working
//        if(startDate != null) {
//            composeTestRule.onNodeWithTag("recurring_modal_pick_date")
//                .performClick()
//
//            composeTestRule.onNodeWithText("1")
//                .performClick()
//
//            composeTestRule.waitSeconds(5)
//        }

        if (intervalN != null) {
            composeTestRule.onNodeWithTag("base_number_input")
                .performTextReplacement(intervalN.toString())
        }

        if (intervalType != null) {
            when (intervalType) {
                IntervalType.DAY -> {
                    clickIntervalArrowLeft()
                    clickIntervalArrowLeft()
                }
                IntervalType.WEEK -> {
                    clickIntervalArrowLeft()
                }
                IntervalType.MONTH -> {
                    //do nothing, it's the default one
                }
                IntervalType.YEAR -> {
                    clickIntervalArrowRight()
                }
            }
        }

        composeTestRule.onNodeWithTag("recurringModalSet")
            .performClick()

        return this
    }

    private fun clickIntervalArrowLeft(): EditPlannedScreen {
        composeTestRule.onNodeWithContentDescription("interval_type_arrow_left")
            .performClick()

        return this
    }

    private fun clickIntervalArrowRight(): EditPlannedScreen {
        composeTestRule.onNodeWithContentDescription("interval_type_arrow_right")
            .performClick()

        return this
    }

    fun <N> addPlannedPayment(
        next: N,
        type: TransactionType,
        amount: String,
        category: String,
        title: String? = null,

        oneTime: Boolean = false,
        startDate: LocalDateTime? = null,
        intervalN: Int? = null,
        intervalType: IntervalType? = null,
    ): N {
        setPaymentType(type = type)
            .enterNumber(amount, next = ChooseCategoryModal(composeTestRule))
            .selectCategory(category, next = this)
            .setRecurring(
                oneTime = oneTime,
                startDate = startDate,
                intervalN = intervalN,
                intervalType = intervalType
            )

        if (title != null) {
            editTitle(newTitle = title)
        }

        return clickSet(next = next)
    }

    fun editTitle(
        newTitle: String
    ): EditPlannedScreen {
        composeTestRule.onNodeWithTag("input_field")
            .performTextReplacement(newTitle)
        return this
    }

    fun <N> clickSet(next: N): N {
        composeTestRule.onNodeWithTag("editPlannedScreen_set")
            .performClick()
        return next
    }


    fun clickDelete(): DeleteConfirmationModal {
        composeTestRule.onNodeWithTag("delete_button")
            .performClick()
        return DeleteConfirmationModal(composeTestRule)
    }

    fun clickRecurringModalPickDate(): CalendarDialog {
        composeTestRule.onNodeWithTag("recurring_modal_pick_date")
            .performClick()
        return CalendarDialog(composeTestRule)
    }
}