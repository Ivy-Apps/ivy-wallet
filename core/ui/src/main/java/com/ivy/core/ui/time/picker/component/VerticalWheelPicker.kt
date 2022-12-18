package com.ivy.core.ui.time.picker.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1Second
import com.ivy.design.l1_buildingBlocks.SpacerVer

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> VerticalWheelPicker(
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

    var autoSelectIndex by remember {
        // skip first spacer
        // skip first item
        // => select the 2nd (center item)
        mutableStateOf(1)
    }
    LaunchedEffect(autoSelectIndex) {
        listState.animateScrollToItem(index = autoSelectIndex)
    }

    LaunchedEffect(initialIndex) {
        listState.scrollToItem(index = initialIndex)
    }

    LaunchedEffect(selectedIndex) {
        onSelectedChange(items[selectedIndex.coerceIn(0 until itemsCount)])
    }

    val itemSize = 64.dp

    LazyColumn(
        modifier = modifier.height(3 * itemSize),
        state = listState,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    ) {
        item(key = "space_zero") {
            SpacerVer(height = itemSize)
        }
        itemsIndexed(
            items = items,
            key = { index, _ -> index }
        ) { index, item ->
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 128.dp)
                    .height(itemSize)
                    .clip(UI.shapes.squared)
                    .clickable {
                        autoSelectIndex = index
                    },
                contentAlignment = Alignment.Center
            ) {
                B1Second(
                    text = text(item),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    color = if (index == selectedIndex)
                        UI.colors.primary else UI.colorsInverted.pure
                )
            }
        }
        item(key = "space_last") {
            SpacerVer(height = itemSize)
        }
    }
}
