package com.ivy.home.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberContrast
import com.ivy.design.l1_buildingBlocks.*
import com.ivy.design.util.ComponentPreview
import com.ivy.resources.R

@Composable
internal fun IncomeExpense(
    income: ValueUi,
    expense: ValueUi,
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.weight(1f),
            icon = R.drawable.ic_income,
            bgColor = UI.colors.green,
            text = stringResource(R.string.income),
            value = income,
            onClick = onIncomeClick,
        )
        SpacerHor(width = 12.dp)
        Card(
            modifier = Modifier.weight(1f),
            icon = R.drawable.ic_expense,
            bgColor = UI.colors.red,
            text = stringResource(R.string.expense),
            value = expense,
            onClick = onExpenseClick,
        )
    }
}

@Composable
private fun Card(
    @DrawableRes
    icon: Int,
    bgColor: Color,
    text: String,
    value: ValueUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(bgColor, UI.shapes.rounded)
            .clip(UI.shapes.rounded)
            .clickable(onClick = onClick)
            .padding(all = 12.dp),
    ) {
        val textColor = rememberContrast(bgColor)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconRes(icon = icon, tint = textColor)
            SpacerHor(width = 4.dp)
            Caption(text = text, color = textColor)
        }
        SpacerVer(height = 4.dp)
        // Amount
        B1Second(
            text = value.amount,
            modifier = Modifier
                .testTag("amount")
                .padding(start = 8.dp),
            fontWeight = FontWeight.Bold,
            color = textColor,
        )
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        IncomeExpense(
            income = dummyValueUi("168.32k"),
            expense = dummyValueUi("9050.14"),
            onIncomeClick = {},
            onExpenseClick = {}
        )
    }
}
// endregion