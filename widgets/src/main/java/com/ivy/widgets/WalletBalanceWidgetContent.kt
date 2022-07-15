package com.ivy.widgets

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.ivy.base.stringRes

@Composable
fun WalletBalanceWidgetContent(
    appLocked: Boolean,
    balance: String,
    currency: String,
    income: String,
    expense: String
) {
    Box(
        GlanceModifier
            .background(ImageProvider(R.drawable.shape_widget_background))
            .clickable(actionRunCallback<WalletBalanceWidgetClickAction>())
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
        ) {
            if (appLocked) {
                Text(
                    modifier = GlanceModifier.fillMaxSize(),
                    text = "App locked",
                    style = TextStyle(
                        fontSize = 30.sp,
                        color = ColorProvider(Color.White),
                        textAlign = TextAlign.Center
                    )
                )
            } else {
                BalanceSection(balance, currency)
                IncomeExpenseSection(income, expense, currency)
                ButtonsSection()
            }
        }
    }
}

@Composable
fun RowScope.WidgetClickableItem(
    @DrawableRes image: Int,
    @StringRes text: Int,
) {
    Column(
        GlanceModifier
            .defaultWeight()
            .clickable(
                actionRunCallback<WalletBalanceButtonsAction>(
                    parameters = actionParametersOf(
                        walletBtnActParam to when (text) {
                            R.string.income -> AddTransactionWidgetClick.ACTION_ADD_INCOME
                            R.string.expense -> AddTransactionWidgetClick.ACTION_ADD_EXPENSE
                            R.string.transfer -> AddTransactionWidgetClick.ACTION_ADD_TRANSFER
                            else -> return
                        }
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = GlanceModifier.size(52.dp),
            provider = ImageProvider(image),
            contentDescription = null
        )
//        Spacer(GlanceModifier.height(8.dp))
//        Text(
//            text = stringRes(text),
//                    style = TextStyle(
//                fontSize = 12.sp,
//                fontWeight = FontWeight.Bold,
//                color = ColorProvider(Color.White)
//            )
//        )
    }
}

@Composable
fun BalanceSection(
    balance: String,
    currency: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.padding(start = 14.dp, top = 14.dp),
    ) {
        Text(
            text = currency,
            style = TextStyle(
                fontSize = 30.sp,
                color = ColorProvider(Color.White)
            )
        )
        Spacer(GlanceModifier.width(10.dp))
        Text(
            text = balance,
            style = TextStyle(
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = ColorProvider(Color.White)
            )
        )
    }
}

@Composable
fun IncomeExpenseSection(
    income: String,
    expense: String,
    currency: String
) {
    Row(
        GlanceModifier.fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            GlanceModifier
                .padding(10.dp)
                .defaultWeight()
                .background(ImageProvider(R.drawable.income_shape_widget_backgroud)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(ImageProvider(R.drawable.ic_income_white), stringRes(R.string.income))
            Spacer(GlanceModifier.width(8.dp))
            Text(
                text = income,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(Color.White)
                )
            )
            Spacer(GlanceModifier.width(4.dp))
            Text(
                text = currency,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = ColorProvider(Color.White),
                )
            )
        }
        Spacer(GlanceModifier.width(8.dp))
        Row(
            GlanceModifier
                .padding(10.dp)
                .defaultWeight()
                .background(ImageProvider(R.drawable.expense_shape_widget_backgroun)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(ImageProvider(R.drawable.ic_expense), stringRes(R.string.expense))
            Spacer(GlanceModifier.width(8.dp))
            Text(
                text = expense,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(Color.Black)
                )
            )
            Spacer(GlanceModifier.width(4.dp))
            Text(
                text = currency,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = ColorProvider(Color.Black)
                )
            )
        }
    }
}

@Composable
fun ButtonsSection() {
    val buttons = listOf(
        R.drawable.ic_widget_income to R.string.income,
        R.drawable.ic_widget_expense to R.string.expense,
        R.drawable.ic_widget_transfer to R.string.transfer
    )
    Row(
        GlanceModifier.fillMaxWidth().padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        buttons.forEach { (image, text) ->
            WidgetClickableItem(image = image, text = text)
        }
    }
}