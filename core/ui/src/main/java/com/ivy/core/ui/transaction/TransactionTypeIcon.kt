package com.ivy.core.ui.transaction

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.base.R
import com.ivy.common.dateNowUTC
import com.ivy.common.timeNowUTC
import com.ivy.core.functions.account.dummyAcc
import com.ivy.core.functions.transaction.dummyTrn
import com.ivy.core.functions.transaction.dummyTrnValue
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnTime
import com.ivy.design.l0_system.*
import com.ivy.design.utils.ComponentPreview
import java.time.LocalDateTime

@Composable
fun Transaction.TrnTypeIcon() {
    val style = iconStyle()
    Icon(
        modifier = Modifier
            .background(style.gradient, CircleShape),
        painter = painterResource(style.icon),
        tint = style.tint,
        contentDescription = "transactionType"
    )
}

@Composable
private fun Transaction.iconStyle(): StyledIcon {
    val isLight = UI.colors.isLight

    return remember(type, time) {
        when (type) {
            is TransactionType.Income -> {
                StyledIcon(
                    icon = R.drawable.ic_income,
                    gradient = GradientGreen.asHorizontalBrush(),
                    tint = White,
                )
            }
            is TransactionType.Expense -> {
                when (val time = time) {
                    is TrnTime.Due -> {
                        dueExpenseStyle(
                            lightTheme = isLight,
                            due = time.due
                        )
                    }
                    is TrnTime.Actual -> {
                        //Actual Expense
                        StyledIcon(
                            icon = R.drawable.ic_expense,
                            gradient = Gradient.neutral(lightTheme = isLight)
                                .asHorizontalBrush(),
                            tint = White,
                        )
                    }
                }
            }
            is TransactionType.Transfer -> {
                //Transfer
                StyledIcon(
                    icon = R.drawable.ic_transfer,
                    gradient = GradientPurple.asHorizontalBrush(),
                    tint = White,
                )
            }
        }
    }
}

private fun dueExpenseStyle(
    lightTheme: Boolean,
    due: LocalDateTime
): StyledIcon = when {
    due.isAfter(timeNowUTC()) -> {
        // Upcoming Expense
        StyledIcon(
            icon = R.drawable.ic_expense,
            gradient = GradientOrangeRevert.asHorizontalBrush(),
            tint = White,
        )
    }
    due.isBefore(dateNowUTC().atStartOfDay()) -> {
        // Overdue Expense
        StyledIcon(
            icon = R.drawable.ic_overdue,
            gradient = GradientRed.asHorizontalBrush(),
            tint = White,
        )
    }
    else -> {
        // Due today
        StyledIcon(
            icon = R.drawable.ic_expense,
            gradient = Gradient.neutral(lightTheme = lightTheme)
                .asHorizontalBrush(),
            tint = White,
        )
    }
}

private data class StyledIcon(
    @DrawableRes
    val icon: Int,
    val gradient: Brush,
    val tint: Color,
)

// region Previews
@Preview
@Composable
private fun Preview_Income() {
    ComponentPreview {
        dummyTrn(
            type = TransactionType.Income
        ).TrnTypeIcon()
    }
}

@Preview
@Composable
private fun Preview_Transfer() {
    ComponentPreview {
        dummyTrn(
            type = TransactionType.Transfer(
                toValue = dummyTrnValue(),
                toAccount = dummyAcc()
            )
        ).TrnTypeIcon()
    }
}

@Preview
@Composable
private fun Preview_ActualExpense() {
    ComponentPreview {
        dummyTrn(
            type = TransactionType.Expense,
            time = TrnTime.Actual(timeNowUTC())
        ).TrnTypeIcon()
    }
}

@Preview
@Composable
private fun Preview_UpcomingExpense() {
    ComponentPreview {
        dummyTrn(
            type = TransactionType.Expense,
            time = TrnTime.Due(timeNowUTC().plusDays(2))
        ).TrnTypeIcon()
    }
}

@Preview
@Composable
private fun Preview_OverdueExpense() {
    ComponentPreview {
        dummyTrn(
            type = TransactionType.Expense,
            time = TrnTime.Due(timeNowUTC().minusDays(2))
        ).TrnTypeIcon()
    }
}

@Preview
@Composable
private fun Preview_DueTodayExpense() {
    ComponentPreview {
        dummyTrn(
            type = TransactionType.Expense,
            time = TrnTime.Due(timeNowUTC().minusHours(1))
        ).TrnTypeIcon()
    }
}
//endregion