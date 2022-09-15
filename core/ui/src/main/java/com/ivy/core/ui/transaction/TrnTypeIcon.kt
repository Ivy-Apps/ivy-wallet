package com.ivy.core.ui.transaction
//
//import androidx.annotation.DrawableRes
//import androidx.compose.foundation.background
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.Icon
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import com.ivy.base.R
//import com.ivy.common.timeNowUTC
//import com.ivy.core.domain.functions.dummy.dummyActual
//import com.ivy.core.domain.functions.dummy.dummyDue
//import com.ivy.core.domain.functions.dummy.dummyTransfer
//import com.ivy.core.ui.transaction.util.TrnDetailedType
//import com.ivy.core.ui.transaction.util.TrnDetailedType.*
//import com.ivy.core.ui.transaction.util.detailedType
//import com.ivy.design.l0_system.*
//import com.ivy.design.utils.ComponentPreviewBase
//
//@Composable
//internal fun TrnTypeIcon(
//    trnDetailedType: TrnDetailedType
//) {
//    val style = trnDetailedType.iconStyle()
//    Icon(
//        modifier = Modifier
//            .background(style.gradient, CircleShape),
//        painter = painterResource(style.icon),
//        tint = style.tint,
//        contentDescription = "transactionType"
//    )
//}
//
//@Composable
//private fun TrnDetailedType.iconStyle(): StyledIcon = when (this) {
//    ActualIncome, UpcomingIncome,
//    OverdueIncome -> StyledIcon(
//        icon = R.drawable.ic_income,
//        gradient = GradientGreen.asHorizontalBrush(),
//        tint = White,
//    )
//    Transfer -> StyledIcon(
//        icon = R.drawable.ic_transfer,
//        gradient = GradientPurple.asHorizontalBrush(),
//        tint = White,
//    )
//    ActualExpense -> StyledIcon(
//        icon = R.drawable.ic_expense,
//        gradient = Gradient.neutral(lightTheme = UI.colors.isLight)
//            .asHorizontalBrush(),
//        tint = White,
//    )
//    UpcomingExpense -> StyledIcon(
//        icon = R.drawable.ic_expense,
//        gradient = GradientOrangeRevert.asHorizontalBrush(),
//        tint = White,
//    )
//    OverdueExpense -> StyledIcon(
//        icon = R.drawable.ic_overdue,
//        gradient = GradientRed.asHorizontalBrush(),
//        tint = White,
//    )
//}
//
//private data class StyledIcon(
//    @DrawableRes
//    val icon: Int,
//    val gradient: Brush,
//    val tint: Color,
//)
//
//// region Previews
//@Preview
//@Composable
//private fun Preview_Income() {
//    ComponentPreviewBase {
//        TrnTypeIcon(
//            detailedType(
//                type = TransactionType.Income,
//                time = dummyActual()
//            )
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun Preview_Transfer() {
//    ComponentPreviewBase {
//        TrnTypeIcon(
//            detailedType(
//                type = dummyTransfer(),
//                time = dummyActual()
//            )
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun Preview_ActualExpense() {
//    ComponentPreviewBase {
//        TrnTypeIcon(
//            detailedType(
//                type = TransactionType.Expense,
//                time = dummyActual()
//            )
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun Preview_UpcomingExpense() {
//    ComponentPreviewBase {
//        TrnTypeIcon(
//            detailedType(
//                type = TransactionType.Expense,
//                time = dummyDue(time = timeNowUTC().plusDays(2))
//            )
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun Preview_OverdueExpense() {
//    ComponentPreviewBase {
//        TrnTypeIcon(
//            detailedType(
//                type = TransactionType.Expense,
//                time = dummyDue(time = timeNowUTC().minusDays(2))
//            )
//        )
//    }
//}
////endregion