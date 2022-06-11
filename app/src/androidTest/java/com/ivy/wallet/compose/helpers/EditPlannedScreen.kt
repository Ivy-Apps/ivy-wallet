package com.ivy.wallet.compose.helpers

import androidx.compose.ui.test.*
import com.ivy.wallet.compose.IvyComposeTestRule
import com.ivy.wallet.domain.data.IntervalType
import com.ivy.wallet.domain.data.TransactionType
import java.time.LocalDateTime

class EditPlannedScreen(
    private val composeTestRule: IvyComposeTestRule
) {
    private val amountInput = IvyAmountInput(composeTestRule)
    private val chooseCategoryModal = ChooseCategoryModal(composeTestRule)

    fun setPaymentType(type: TransactionType) {
        val nodeText = when (type) {
            TransactionType.INCOME -> "modal_type_${type.name}"
            TransactionType.EXPENSE -> "modal_type_${type.name}"
            TransactionType.TRANSFER -> error("Unsupported type for planned payments")
        }
        composeTestRule.onNodeWithTag(nodeText)
            .performClick()
    }

    fun setRecurring(
        oneTime: Boolean,
        startDate: LocalDateTime?,
        intervalN: Int?,
        intervalType: IntervalType?,
    ) {
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
    }

    private fun clickIntervalArrowLeft() {
        composeTestRule.onNodeWithContentDescription("interval_type_arrow_left")
            .performClick()
    }

    private fun clickIntervalArrowRight() {
        composeTestRule.onNodeWithContentDescription("interval_type_arrow_right")
            .performClick()
    }

    fun addPlannedPayment(
        type: TransactionType,
        amount: String,
        category: String,
        title: String? = null,

        oneTime: Boolean = false,
        startDate: LocalDateTime? = null,
        intervalN: Int? = null,
        intervalType: IntervalType? = null,
    ) {
        setPaymentType(type = type)

        amountInput.enterNumber(amount)

        chooseCategoryModal.selectCategory(category)

        setRecurring(
            oneTime = oneTime,
            startDate = startDate,
            intervalN = intervalN,
            intervalType = intervalType
        )

        if (title != null) {
            editTitle(newTitle = title)
        }

        clickSet()
    }

    fun editTitle(
        newTitle: String
    ) {
        composeTestRule.onNodeWithTag("input_field")
            .performTextReplacement(newTitle)
    }

    fun clickSet() {
        composeTestRule.onNodeWithTag("editPlannedScreen_set")
            .performClick()
    }

    fun clickGet() {
        composeTestRule.onNodeWithText("Get")
            .performClick()
    }

    fun clickPay() {
        composeTestRule.onNodeWithText("Pay")
            .performClick()
    }

    fun clickDelete() {
        composeTestRule.onNodeWithTag("delete_button")
            .performClick()
    }
}