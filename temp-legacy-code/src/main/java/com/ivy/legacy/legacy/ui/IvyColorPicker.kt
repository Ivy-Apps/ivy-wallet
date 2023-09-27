package com.ivy.domain.legacy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.design.IVY_COLOR_PICKER_COLORS_FREE
import com.ivy.design.IVY_COLOR_PICKER_COLORS_PREMIUM
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.dynamicContrast
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.IvyIcon
import com.ivy.design.utils.densityScope
import com.ivy.design.utils.thenIf
import com.ivy.frp.test.TestingContext
import com.ivy.legacy.frp.onScreenStart
import com.ivy.legacy.ivyWalletCtx
import com.ivy.navigation.navigation
import com.ivy.resources.R
import kotlinx.coroutines.launch

@Deprecated("Old design system. Use `:ivy-design` and Material3")
private data class IvyColor(
    val color: Color,
    val premium: Boolean
)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun ColumnScope.IvyColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    Text(
        modifier = Modifier.padding(horizontal = 32.dp),
        text = stringResource(R.string.choose_color),
        style = UI.typo.b2.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.ExtraBold
        )
    )

    Spacer(Modifier.height(16.dp))

    val freeIvyColors = IVY_COLOR_PICKER_COLORS_FREE
        .map {
            IvyColor(
                color = it,
                premium = false
            )
        }

    val premiumIvyColors = IVY_COLOR_PICKER_COLORS_PREMIUM
        .map {
            IvyColor(
                color = it,
                premium = true
            )
        }

    val ivyColors = freeIvyColors + premiumIvyColors

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    densityScope {
        onScreenStart {
            if (TestingContext.inTest) return@onScreenStart // listState.scrollToItem breaks the tests
            // java.lang.IllegalStateException: pending composition has not been applied

            val selectedColorIndex = ivyColors.indexOfFirst { it.color == selectedColor }
            if (selectedColorIndex != -1) {
                coroutineScope.launch {
                    listState.scrollToItem(
                        index = selectedColorIndex,
                        scrollOffset = 0
                    )
                }
            }
        }
    }

    val ivyContext = ivyWalletCtx()
    val navigation = navigation()

    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        state = listState
    ) {
        items(
            count = ivyColors.size
        ) { index ->
            ColorItem(
                index = index,
                ivyColor = ivyColors[index],
                selectedColor = selectedColor,
                onSelected = {
                    onColorSelected(it.color)
                }
            )
        }
    }
}

@Composable
private fun ColorItem(
    index: Int,
    ivyColor: IvyColor,
    selectedColor: Color,
    onSelected: (IvyColor) -> Unit
) {
    val color = ivyColor.color
    val selected = color == selectedColor

    if (index == 0) {
        Spacer(Modifier.width(24.dp))
    }

    val ivyContext = ivyWalletCtx()
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(48.dp)
            .background(color, CircleShape)
            .thenIf(selected) {
                border(width = 4.dp, color = color.dynamicContrast(), CircleShape)
            }
            .clickable(onClick = {
                onSelected(ivyColor)
            })
            .testTag("color_item_${ivyColor.color.value}"),
        contentAlignment = Alignment.Center
    ) {
        if (ivyColor.premium && !ivyContext.isPremium) {
            IvyIcon(
                icon = R.drawable.ic_custom_safe_s,
                tint = color.dynamicContrast()
            )
        }
    }

    Spacer(Modifier.width(if (selected) 16.dp else 24.dp))
}