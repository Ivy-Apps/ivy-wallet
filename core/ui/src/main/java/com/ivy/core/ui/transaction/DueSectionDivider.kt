package com.ivy.core.ui.transaction

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.ivy.base.R
import com.ivy.core.functions.transaction.dummyOverdueSection
import com.ivy.core.functions.transaction.dummyUpcomingSection
import com.ivy.core.functions.transaction.dummyValue
import com.ivy.core.ui.temp.ComponentPreview
import com.ivy.core.ui.value.formatAmount
import com.ivy.data.transaction.OverdueSection
import com.ivy.data.transaction.UpcomingSection
import com.ivy.data.transaction.Value
import com.ivy.design.l0_system.*
import com.ivy.design.l1_buildingBlocks.IvyIcon
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l3_ivyComponents.IvyDividerDot
import com.ivy.design.utils.clickableNoIndication
import com.ivy.design.utils.springBounce

@Composable
fun UpcomingSection.SectionDivider(
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit
) {
    DueSectionDivider(
        expanded = expanded,
        setExpanded = setExpanded,
        title = stringResource(R.string.upcoming),
        titleColor = Orange,
        income = income,
        expense = expense,
    )
}

@Composable
fun OverdueSection.SectionDivider(
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit
) {
    DueSectionDivider(
        expanded = expanded,
        setExpanded = setExpanded,
        title = stringResource(R.string.overdue),
        titleColor = Red,
        income = income,
        expense = expense,
    )
}

@Composable
private fun DueSectionDivider(
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,

    title: String,
    titleColor: Color,
    income: Value,
    expense: Value,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableNoIndication {
                setExpanded(!expanded)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpacerHor(width = 24.dp)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Title(title = title, titleColor = titleColor)
            SpacerVer(height = 4.dp)
            DueIncomeExpense(income = income, expense = expense)
        }

        ExpandCollapseIcon(expanded = expanded)
        SpacerHor(width = 32.dp)
    }
}

@Composable
private fun Title(
    title: String,
    titleColor: Color,
) {
    Text(
        modifier = Modifier
            .testTag("upcoming_title"),
        text = title,
        style = UI.typo.b1.style(
            fontWeight = FontWeight.ExtraBold,
            color = titleColor
        )
    )
}

@Composable
private fun ExpandCollapseIcon(
    expanded: Boolean,
) {
    val expandIconRotation by animateFloatAsState(
        targetValue = if (expanded) 0f else -180f,
        animationSpec = springBounce()
    )
    // TODO: Optimize by setting default to arrow up (collapsed state)
    IvyIcon(
        modifier = Modifier.rotate(expandIconRotation),
        icon = R.drawable.ic_expandarrow
    )
}

@Composable
private fun DueIncomeExpense(
    income: Value,
    expense: Value,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (expense.amount > 0) {
            expense.AmountCurrencyLabel(
                testTag = "upcoming_expense",
                label = stringResource(R.string.expenses_lowercase),
                amountColor = UI.colors.pureInverse,
            )
        }

        if (income.amount > 0 && expense.amount > 0) {
            SpacerHor(width = 8.dp)
            IvyDividerDot()
            SpacerHor(width = 8.dp)
        }

        if (income.amount > 0) {
            income.AmountCurrencyLabel(
                testTag = "upcoming_income",
                label = stringResource(R.string.income_lowercase),
                amountColor = Green,
            )
        }
    }
}

@Composable
private fun Value.AmountCurrencyLabel(
    testTag: String,
    label: String,
    amountColor: Color
) {
    val formattedAmount = formatAmount(shortenBigNumbers = false)
    Text(
        modifier = Modifier.testTag(testTag),
        text = "$formattedAmount $currency",
        style = UI.typo.nC.style(
            fontWeight = FontWeight.ExtraBold,
            color = amountColor,
        )
    )
    SpacerHor(width = 4.dp)
    Text(
        text = label,
        style = UI.typo.c.style(
            fontWeight = FontWeight.Normal,
            color = UI.colors.pureInverse
        )
    )
}


// region Previews
@Preview
@Composable
private fun Preview_Upcoming_IncomeExpenses() {
    ComponentPreview {
        dummyUpcomingSection(
            income = dummyValue(8043.23, "BGN"),
            expense = dummyValue(923.87, "BGN")
        ).SectionDivider(
            expanded = true,
            setExpanded = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Overdue_Expenses() {
    ComponentPreview {
        dummyOverdueSection(
            income = dummyValue(0.0, "BGN"),
            expense = dummyValue(923.87, "BGN")
        ).SectionDivider(
            expanded = false,
            setExpanded = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Upcoming_Income() {
    ComponentPreview {
        dummyUpcomingSection(
            income = dummyValue(8043.23, "BGN"),
            expense = dummyValue(0.0, "BGN")
        ).SectionDivider(
            expanded = false,
            setExpanded = {}
        )
    }
}
// endregion