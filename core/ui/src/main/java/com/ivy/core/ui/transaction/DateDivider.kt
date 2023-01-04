package com.ivy.core.ui.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.common.time.dateId
import com.ivy.common.time.dateNowLocal
import com.ivy.core.domain.action.calculate.transaction.toggleTrnListDate
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.data.transaction.TrnListItemUi
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.B2Second
import com.ivy.design.l1_buildingBlocks.Caption
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.thenIf

@Composable
fun DateDivider(divider: TrnListItemUi.DateDivider) {
    val primary = UI.colors.primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                toggleTrnListDate(dateId = divider.id)
            }
            .thenIf(divider.collapsed) {
                drawBehind {
                    val cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                    val path = Path().apply {
                        addRoundRect(
                            RoundRect(
                                rect = Rect(
                                    offset = Offset.Zero,
                                    size = Size(
                                        width = 8.dp.toPx(),
                                        height = size.height
                                    ),
                                ),
                                topRight = cornerRadius,
                                bottomRight = cornerRadius,
                            )
                        )
                    }
                    drawPath(path, color = primary)
                }
            }
            .padding(start = 24.dp, end = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Date(date = divider.date, day = divider.day)
        Cashflow(cashflow = divider.cashflow, positiveCashflow = divider.positiveCashflow)
    }
}

@Composable
private fun RowScope.Date(
    date: String,
    day: String,
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {
        B1(text = date, fontWeight = FontWeight.ExtraBold)
        Caption(text = day, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Cashflow(
    cashflow: ValueUi,
    positiveCashflow: Boolean,
) {
    B2Second(
        text = "${cashflow.amount} ${cashflow.currency}",
        color = if (positiveCashflow) UI.colors.green else UI.colors.neutral
    )
}


// region Previews
@Preview
@Composable
private fun Preview_Positive() {
    ComponentPreview {
        DateDivider(
            TrnListItemUi.DateDivider(
                id = dateNowLocal().dateId(),
                date = "September 25.",
                day = "Today",
                cashflow = dummyValueUi("154.32"),
                positiveCashflow = true,
                collapsed = false,
            )
        )
    }
}

@Preview
@Composable
private fun Preview_Negative() {
    ComponentPreview {
        DateDivider(
            TrnListItemUi.DateDivider(
                id = dateNowLocal().dateId(),
                date = "September 25. 2020",
                day = "Today",
                cashflow = dummyValueUi("-1k"),
                positiveCashflow = false,
                collapsed = true,
            )
        )
    }
}
// endregion