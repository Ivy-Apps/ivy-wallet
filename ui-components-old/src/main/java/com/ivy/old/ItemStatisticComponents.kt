package com.ivy.old

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.data.IvyCurrency
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.utils.drawColoredShadow
import com.ivy.wallet.utils.format

@Composable
fun IncomeExpensesCards(
    history: List<Any>,
    currency: String,
    income: Double,
    expenses: Double,

    hasAddButtons: Boolean,
    itemColor: Color,

    incomeHeaderCardClicked: () -> Unit = {},
    expenseHeaderCardClicked: () -> Unit = {},
    onAddTransaction: (TrnType) -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        HeaderCard(
            title = com.ivy.core.ui.temp.stringRes(R.string.income_uppercase),
            currencyCode = currency,
            amount = income,
            transactionCount = history
                .filterIsInstance(TransactionOld::class.java)
                .count { it.type == TrnType.INCOME },
            addButtonText = if (hasAddButtons) stringResource(R.string.add_income) else null,
            isIncome = true,

            itemColor = itemColor,
            onHeaderCardClicked = { incomeHeaderCardClicked() }
        ) {
            onAddTransaction(TrnType.INCOME)
        }

        Spacer(Modifier.width(12.dp))

        HeaderCard(
            title = com.ivy.core.ui.temp.stringRes(R.string.expenses_uppercase),
            currencyCode = currency,
            amount = expenses,
            transactionCount = history
                .filterIsInstance(TransactionOld::class.java)
                .count { it.type == TrnType.EXPENSE },
            addButtonText = if (hasAddButtons) stringResource(R.string.add_expense) else null,
            isIncome = false,

            itemColor = itemColor,
            onHeaderCardClicked = { expenseHeaderCardClicked() }
        ) {
            onAddTransaction(TrnType.EXPENSE)
        }

        Spacer(Modifier.width(16.dp))
    }
}

@Composable
private fun RowScope.HeaderCard(
    title: String,
    currencyCode: String,
    amount: Double,
    transactionCount: Int,

    isIncome: Boolean,
    addButtonText: String?,

    itemColor: Color,

    onHeaderCardClicked: () -> Unit = {},
    onAddClick: () -> Unit
) {
    val backgroundColor = if (isDarkColor(itemColor))
        MediumBlack.copy(alpha = 0.9f) else MediumWhite.copy(alpha = 0.9f)

    val contrastColor = findContrastTextColor(backgroundColor)

    Column(
        modifier = Modifier
            .weight(1f)
            .drawColoredShadow(
                color = backgroundColor,
                alpha = 0.1f
            )
            .background(backgroundColor, UI.shapes.r2)
            .clickable { onHeaderCardClicked() },
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = title,
            style = UI.typo.c.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(12.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = amount.format(currencyCode),
            style = UI.typo.nB1.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = IvyCurrency.fromCode(currencyCode)?.name ?: "",
            style = UI.typo.b2.style(
                color = contrastColor,
                fontWeight = FontWeight.Normal
            )
        )

        Spacer(Modifier.height(12.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = transactionCount.toString(),
            style = UI.typo.nB1.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = com.ivy.core.ui.temp.stringRes(R.string.transactions),
            style = UI.typo.b2.style(
                color = contrastColor,
                fontWeight = FontWeight.Normal
            )
        )

        Spacer(Modifier.height(24.dp))

        if (addButtonText != null) {
            val addButtonBackground = if (isIncome) Green else contrastColor
            IvyButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally),
                text = addButtonText,
                shadowAlpha = 0.1f,
                backgroundGradient = Gradient.solid(addButtonBackground),
                textStyle = UI.typo.b2.style(
                    color = findContrastTextColor(addButtonBackground),
                    fontWeight = FontWeight.Bold
                ),
                wrapContentMode = false
            ) {
                onAddClick()
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
