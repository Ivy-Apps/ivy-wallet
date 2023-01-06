package com.ivy.core.ui.time.picker.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1Second
import com.ivy.design.l1_buildingBlocks.SpacerHor

private val ITEM_HEIGHT = 64.dp
private val ITEM_WIDTH = 104.dp
private val SELECTOR_LINES_PADDING_FROM_CENTER = 16.dp
private val SELECTOR_LINE_WIDTH = 2.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> HorizontalWheelPicker(
    items: List<T>,
    initialIndex: Int,
    itemsCount: Int,
    text: (T) -> String,
    modifier: Modifier = Modifier,
    onSelectedChange: (T) -> Unit,
) {
    val listState = rememberLazyListState()
    val selectedIndex by remember {
        derivedStateOf {
            (listState.firstVisibleItemIndex)
                .coerceIn(0 until itemsCount)
        }
    }
    LaunchedEffect(selectedIndex) {
        onSelectedChange(items[selectedIndex.coerceIn(0 until itemsCount)])
    }

    LaunchedEffect(initialIndex) {
        listState.scrollToItem(index = initialIndex)
    }

    var selectIndexOnClick by remember {
        // skip first spacer
        // skip first item
        // => select the 2nd (center item)
        mutableStateOf<Int?>(null)
    }
    LaunchedEffect(selectIndexOnClick) {
        selectIndexOnClick?.let {
            listState.animateScrollToItem(it)
            // reset, so the same index can be re-selected
            selectIndexOnClick = null
        }
    }

    val primary = UI.colors.primary
    LazyRow(
        modifier = modifier
            .width(3 * ITEM_WIDTH)
            .drawWithCache {
                onDrawBehind {
                    val halfItem = ITEM_WIDTH.value / 2
                    val padding = SELECTOR_LINES_PADDING_FROM_CENTER.toPx()
                    val lineWidth = SELECTOR_LINE_WIDTH.toPx()
                    drawLine(
                        start = Offset(x = center.x - halfItem - padding, y = 0f),
                        end = Offset(x = center.x - halfItem - padding, y = size.height),
                        strokeWidth = lineWidth,
                        color = primary,
                        cap = StrokeCap.Butt
                    )
                    drawLine(
                        start = Offset(x = center.x + halfItem + padding, y = 0f),
                        end = Offset(x = center.x + halfItem + padding, y = size.height),
                        strokeWidth = lineWidth,
                        color = primary,
                        cap = StrokeCap.Butt
                    )
                }
            },
        state = listState,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    ) {
        item(key = "space_zero") {
            SpacerHor(width = ITEM_WIDTH)
        }
        itemsIndexed(
            items = items,
            key = { index, _ -> index }
        ) { index, item ->
            Box(
                modifier = Modifier
                    .defaultMinSize(minHeight = ITEM_HEIGHT)
                    .width(ITEM_WIDTH)
                    .clip(UI.shapes.squared)
                    .clickable {
                        selectIndexOnClick = index
                    },
                contentAlignment = Alignment.Center
            ) {
                val selected = index == selectedIndex
                B1Second(
                    text = text(item),
                    fontWeight = FontWeight.Bold,
                    color = if (selected)
                        UI.colors.primary else UI.colorsInverted.pure,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
        item(key = "space_last") {
            SpacerHor(width = ITEM_WIDTH)
        }
    }
}
