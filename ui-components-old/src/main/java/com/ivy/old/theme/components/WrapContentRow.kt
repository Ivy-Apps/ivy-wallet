package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.data.CategoryOld
import com.ivy.design.l0_system.UI
import com.ivy.wallet.ui.theme.Ivy


@Composable
fun <T> WrapContentRow(
    modifier: Modifier = Modifier,
    items: List<T>,
    verticalMarginBetweenRows: Dp = 8.dp,
    horizontalMarginBetweenItems: Dp = 8.dp,
    ItemContent: @Composable (item: T) -> Unit
) {
    if (items.isEmpty()) return

    Layout(
        modifier = modifier,
        content = {
            for (item in items) {
                ItemContent(item)
            }
        }
    ) { measurables, constraints ->
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        var x = 0

        val placeables = measurables.map {
            it.measure(childConstraints)
        }
        val itemHeight = placeables.maxOfOrNull { it.height } ?: 0

        var height = 0

        for (placeable in placeables) {
            if (x + placeable.width > constraints.maxWidth) {
                //item is overflowing -> move it to a new row
                x = 0
                height += itemHeight + verticalMarginBetweenRows.roundToPx()
                x += placeable.width + horizontalMarginBetweenItems.roundToPx()
                continue
            }

            x += placeable.width + horizontalMarginBetweenItems.roundToPx()
        }

        height += itemHeight


        layout(constraints.maxWidth, height) {
            //Reset x
            x = 0
            var y = 0

            placeables.forEach { placeable ->
                if (x + placeable.width > constraints.maxWidth) {
                    //item is overflowing -> move it to a new row
                    x = 0
                    y += itemHeight + verticalMarginBetweenRows.roundToPx()
                }

                placeable.place(x, y)
                x += placeable.width + horizontalMarginBetweenItems.roundToPx()
            }
        }
    }
}

@Preview
@Composable
private fun PreviewWrapContentRow() {
    com.ivy.core.ui.temp.Preview {
        WrapContentRow(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .background(UI.colors.red),
            items = listOf(
                CategoryOld("Todo", color = Ivy.toArgb()),
                CategoryOld("Ivy", color = Ivy.toArgb()),
                CategoryOld("Qredo", color = Ivy.toArgb()),
                CategoryOld("Home", color = Ivy.toArgb()),
                CategoryOld("Inspiration", color = Ivy.toArgb()),
                CategoryOld("Business and marketing", color = Ivy.toArgb()),
                CategoryOld("Testdfsgdfgdf", color = Ivy.toArgb()),
            ),
            verticalMarginBetweenRows = 8.dp
        ) {
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(UI.colors.medium, RoundedCornerShape(8.dp))
                    .clickable(onClick = { })
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                text = it.name,
                style = TextStyle(
                    color = UI.colors.mediumInverse,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}