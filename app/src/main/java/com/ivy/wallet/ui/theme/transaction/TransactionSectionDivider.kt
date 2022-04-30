package com.ivy.wallet.ui.theme.transaction

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R
import com.ivy.wallet.ui.IvyWalletComponentPreview
import com.ivy.wallet.ui.theme.Orange
import com.ivy.wallet.ui.theme.Red
import com.ivy.wallet.ui.theme.components.IvyDividerDot
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.utils.clickableNoIndication
import com.ivy.wallet.utils.format
import com.ivy.wallet.utils.springBounce

@Composable
fun SectionDivider(
    expanded: Boolean,
    title: String,
    titleColor: Color,
    baseCurrency: String,
    income: Double,
    expenses: Double,

    showIncomeExpenseRow: Boolean = true,

    setExpanded: (Boolean) -> Unit
) {
    Spacer(Modifier.height(24.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableNoIndication {
                setExpanded(!expanded)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val expandIconRotation by animateFloatAsState(
            targetValue = if (expanded) 0f else -180f,
            animationSpec = springBounce()
        )

        Spacer(Modifier.width(24.dp))

        Column {
            Text(
                modifier = Modifier.testTag("upcoming_title"),
                text = title,
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = titleColor
                )
            )

            if (showIncomeExpenseRow) {
                Spacer(Modifier.height(4.dp))

                SectionDividerIncomeExpenseRow(
                    income = income,
                    expenses = expenses,
                    baseCurrency = baseCurrency
                )
            } else {
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.weight(1f))

        IvyIcon(
            modifier = Modifier.rotate(expandIconRotation),
            icon = R.drawable.ic_expandarrow
        )

        Spacer(Modifier.width(32.dp))
    }
}

@Composable
private fun SectionDividerIncomeExpenseRow(
    income: Double,
    expenses: Double,
    baseCurrency: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (expenses > 0) {
            Text(
                modifier = Modifier.testTag("upcoming_expense"),
                text = "${expenses.format(baseCurrency)} $baseCurrency",
                style = UI.typo.nC.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = UI.colors.pureInverse
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.expenses_lowercase),
                style = UI.typo.c.style(
                    fontWeight = FontWeight.Normal,
                    color = UI.colors.pureInverse
                )
            )
        }

        if (income > 0 && expenses > 0) {
            Spacer(Modifier.width(8.dp))

            IvyDividerDot()

            Spacer(Modifier.width(8.dp))
        }

        if (income > 0) {
            Text(
                modifier = Modifier.testTag("upcoming_income"),
                text = "${income.format(baseCurrency)} $baseCurrency",
                style = UI.typo.nC.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = UI.colors.green
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.income_lowercase),
                style = UI.typo.c.style(
                    fontWeight = FontWeight.Normal,
                    color = UI.colors.pureInverse
                )
            )
        }
    }
}

@Preview
@Composable
private fun Preview_Income_Expenses() {
    IvyWalletComponentPreview {
        SectionDivider(
            expanded = true,
            title = "Upcoming",
            titleColor = Orange,
            baseCurrency = "BGN",
            income = 8043.23,
            expenses = 923.87
        ) {

        }
    }
}

@Preview
@Composable
private fun Preview_Expenses() {
    IvyWalletComponentPreview {
        SectionDivider(
            expanded = true,
            title = "Overdue",
            titleColor = Red,
            baseCurrency = "BGN",
            income = 0.0,
            expenses = 923.87
        ) {

        }
    }
}

@Preview
@Composable
private fun Preview_Income() {
    IvyWalletComponentPreview {
        SectionDivider(
            expanded = true,
            title = "Upcoming",
            titleColor = Orange,
            baseCurrency = "BGN",
            income = 8043.23,
            expenses = 0.0
        ) {

        }
    }
}