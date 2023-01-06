package com.ivy.core.ui.transaction

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
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
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.data.transaction.DueSectionUi
import com.ivy.core.ui.data.transaction.DueSectionUiType
import com.ivy.core.ui.data.transaction.dummyDueSectionUi
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.IconRes
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l3_ivyComponents.IvyDividerDot
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.springBounce
import com.ivy.resources.R

@Composable
fun DueSectionUi.SectionDivider(
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                setExpanded(!expanded)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpacerHor(width = 24.dp)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Title(
                title = stringResource(
                    when (dueType) {
                        DueSectionUiType.Upcoming -> R.string.upcoming
                        DueSectionUiType.Overdue -> R.string.overdue
                    }
                ),
                color = when (dueType) {
                    DueSectionUiType.Upcoming -> UI.colors.orange
                    DueSectionUiType.Overdue -> UI.colors.red
                }
            )
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
    color: Color,
) {
    B1(
        text = title,
        modifier = Modifier
            // TODO: Rename this to "due_title"
            .testTag("upcoming_title"),
        fontWeight = FontWeight.ExtraBold,
        color = color,
    )
}

@Composable
private fun ExpandCollapseIcon(
    expanded: Boolean,
) {
    val expandIconRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = springBounce()
    )
    IconRes(
        icon = R.drawable.ic_expand_less,
        modifier = Modifier.rotate(expandIconRotation)
    )
}

@Composable
private fun DueIncomeExpense(
    income: ValueUi?,
    expense: ValueUi?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        expense?.AmountCurrencyLabel(
            testTag = "upcoming_expense",
            label = stringResource(R.string.expenses_lowercase),
            amountColor = UI.colorsInverted.pure,
        )

        if (income != null && expense != null) {
            SpacerHor(width = 8.dp)
            IvyDividerDot()
            SpacerHor(width = 8.dp)
        }

        income?.AmountCurrencyLabel(
            testTag = "upcoming_income",
            label = stringResource(R.string.income_lowercase),
            amountColor = UI.colors.green,
        )
    }
}

@Composable
private fun ValueUi.AmountCurrencyLabel(
    testTag: String,
    label: String,
    amountColor: Color
) {
    Text(
        modifier = Modifier.testTag(testTag),
        text = "$amount $currency",
        style = UI.typoSecond.c.style(
            fontWeight = FontWeight.ExtraBold,
            color = amountColor,
        )
    )
    SpacerHor(width = 4.dp)
    Text(
        text = label,
        style = UI.typo.c.style(
            fontWeight = FontWeight.Normal,
            color = UI.colorsInverted.pure
        )
    )
}


// region Previews
@Preview
@Composable
private fun Preview_Upcoming_IncomeExpenses() {
    ComponentPreview {
        dummyDueSectionUi(
            dueType = DueSectionUiType.Upcoming,
            income = dummyValueUi("8043.23"),
            expense = dummyValueUi("923.87")
        ).SectionDivider(
            expanded = false,
            setExpanded = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Overdue_Expenses() {
    ComponentPreview {
        dummyDueSectionUi(
            dueType = DueSectionUiType.Overdue,
            income = null,
            expense = dummyValueUi("923.87")
        ).SectionDivider(
            expanded = true,
            setExpanded = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Upcoming_Income() {
    ComponentPreview {
        dummyDueSectionUi(
            dueType = DueSectionUiType.Upcoming,
            income = dummyValueUi("8043.23"),
            expense = null,
        ).SectionDivider(
            expanded = true,
            setExpanded = {}
        )
    }
}
// endregion