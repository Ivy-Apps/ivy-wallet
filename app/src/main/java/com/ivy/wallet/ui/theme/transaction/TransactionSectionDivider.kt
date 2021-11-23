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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.wallet.R
import com.ivy.wallet.base.clickableNoIndication
import com.ivy.wallet.base.format
import com.ivy.wallet.base.springBounce
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IvyDividerDot
import com.ivy.wallet.ui.theme.components.IvyIcon

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
                style = Typo.body1.style(
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
                style = Typo.numberCaption.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = IvyTheme.colors.pureInverse
                )
            )
            Text(
                modifier = Modifier.padding(bottom = 2.dp),
                text = " expenses",
                style = Typo.caption.style(
                    fontWeight = FontWeight.Normal,
                    color = IvyTheme.colors.pureInverse
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
                style = Typo.numberCaption.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = IvyTheme.colors.green
                )
            )
            Text(
                modifier = Modifier.padding(bottom = 2.dp),
                text = " income",
                style = Typo.caption.style(
                    fontWeight = FontWeight.Normal,
                    color = IvyTheme.colors.pureInverse
                )
            )
        }
    }
}

@Preview
@Composable
private fun Preview_Income_Expenses() {
    IvyComponentPreview {
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
    IvyComponentPreview {
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
    IvyComponentPreview {
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