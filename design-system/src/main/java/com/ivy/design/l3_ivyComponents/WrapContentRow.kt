package com.ivy.design.l3_ivyComponents

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun <T> WrapContentRow(
    items: List<T>,
    itemKey: (T) -> String,
    modifier: Modifier = Modifier,
    horizontalMarginBetweenItems: Dp = 8.dp,
    verticalMarginBetweenRows: Dp = 8.dp,
    itemContent: @Composable (item: T) -> Unit
) {
    if (items.isEmpty()) return

    Layout(
        modifier = modifier,
        content = {
            for (item in items) {
                key(itemKey(item)) {
                    itemContent(item)
                }
            }
        }
    ) { measurables, constraints ->
        val childConstraints = constraints.copy(
            minWidth = 0, minHeight = 0
        )

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