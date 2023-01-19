package com.ivy.core.ui.transaction.item

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
import com.ivy.core.domain.algorithm.trnhistory.toggleCollapseExpandTrnListKey
import com.ivy.core.domain.pure.format.SignedValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.algorithm.trnhistory.data.DateDividerUi
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.B2Second
import com.ivy.design.l1_buildingBlocks.Caption
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.thenIf

@Composable
fun DateDivider(divider: DateDividerUi) {
    val primary = UI.colors.primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                toggleCollapseExpandTrnListKey(keyId = divider.id)
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
        Date(date = divider.date, day = divider.dateContext)
        Cashflow(cashflow = divider.cashflow)
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
    cashflow: SignedValueUi,
) {

    B2Second(
        text = when (cashflow) {
            is SignedValueUi.Negative -> "-${cashflow.value.amount} ${cashflow.value.currency}"
            else -> "${cashflow.value.amount} ${cashflow.value.currency}"
        },
        color = when (cashflow) {
            is SignedValueUi.Positive -> UI.colors.green
            else -> UI.colors.neutral
        }
    )
}


// region Previews
@Preview
@Composable
private fun Preview_Positive() {
    ComponentPreview {
        DateDivider(
            DateDividerUi(
                id = dateNowLocal().dateId(),
                date = "September 25.",
                dateContext = "Today",
                cashflow = SignedValueUi.Positive(dummyValueUi("154.32")),
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
            DateDividerUi(
                id = dateNowLocal().dateId(),
                date = "September 25. 2020",
                dateContext = "Today",
                cashflow = SignedValueUi.Negative(dummyValueUi("154.32")),
                collapsed = false,
            )
        )
    }
}

@Preview
@Composable
private fun Preview_Zero() {
    ComponentPreview {
        DateDivider(
            DateDividerUi(
                id = dateNowLocal().dateId(),
                date = "September 25. 2020",
                dateContext = "Today",
                cashflow = SignedValueUi.Zero(dummyValueUi("0")),
                collapsed = false,
            )
        )
    }
}
// endregion