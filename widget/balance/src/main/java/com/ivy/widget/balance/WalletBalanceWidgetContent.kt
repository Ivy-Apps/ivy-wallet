package com.ivy.widget.balance

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.ivy.ui.R

@Composable
fun WalletBalanceWidgetContent(
    appLocked: Boolean,
    balance: String,
    currency: String,
    income: String,
    expense: String,
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit,
    onTransferClick: () -> Unit,
    onWidgetClick: () -> Unit,
) {
    val resources = LocalContext.current.resources
    Box(
        GlanceModifier
            .background(ImageProvider(R.drawable.shape_widget_background))
            .clickable(onWidgetClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = if (appLocked) Alignment.CenterHorizontally else Alignment.Start
        ) {
            if (appLocked) {
                Text(
                    modifier = GlanceModifier.padding(8.dp),
                    text = resources.getString(R.string.app_locked),
                    style = TextStyle(
                        fontSize = 25.sp,
                        color = ColorProvider(Color.White),
                        textAlign = TextAlign.Center
                    )
                )
            } else {
                BalanceSection(balance, currency)
                IncomeExpenseSection(income, expense, currency)
                ButtonsSection(onIncomeClick, onExpenseClick, onTransferClick)
            }
        }
    }
}

@Composable
fun RowScope.WidgetClickableItem(
    @DrawableRes image: Int,
    onClick: () -> Unit,
) {
    Column(
        GlanceModifier
            .defaultWeight()
            .clickable(onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = GlanceModifier.size(52.dp),
            provider = ImageProvider(image),
            contentDescription = null
        )
    }
}

@Composable
fun BalanceSection(
    balance: String,
    currency: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
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
    currency: String,
) {
    Row(
        GlanceModifier.fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val resources = LocalContext.current.resources
        Row(
            GlanceModifier
                .padding(10.dp)
                .defaultWeight()
                .background(ImageProvider(R.drawable.income_shape_widget_background)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(ImageProvider(R.drawable.ic_income_white), resources.getString((R.string.income)))
            Text(
                text = "$income $currency",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(Color.White)
                )
            )
        }
        Spacer(GlanceModifier.width(8.dp))
        Row(
            GlanceModifier
                .padding(10.dp)
                .defaultWeight()
                .background(ImageProvider(R.drawable.expense_shape_widget_background)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                ImageProvider(R.drawable.ic_expense),
                resources.getString(R.string.expense)
            )
            Text(
                text = "$expense $currency",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(Color.Black)
                )
            )
        }
    }
}

@Composable
fun ButtonsSection(
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit,
    onTransferClick: () -> Unit,
) {
    val buttons = listOf(
        R.drawable.ic_widget_income to R.string.income,
        R.drawable.ic_widget_expense to R.string.expense,
        R.drawable.ic_widget_transfer to R.string.transfer
    )
    Row(
        GlanceModifier.fillMaxWidth().padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        buttons.forEach { (image, text) ->
            WidgetClickableItem(
                image = image,
                onClick = {
                    when (text) {
                        R.string.income -> onIncomeClick()
                        R.string.expense -> onExpenseClick()
                        R.string.transfer -> onTransferClick()
                    }
                }
            )
        }
    }
}
