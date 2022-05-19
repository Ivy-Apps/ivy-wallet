package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.test.TestingContext
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.R
import com.ivy.wallet.ui.IvyWalletComponentPreview
import com.ivy.wallet.ui.ivyWalletCtx
import com.ivy.wallet.ui.paywall.PaywallReason
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.utils.densityScope
import com.ivy.wallet.utils.onScreenStart
import com.ivy.wallet.utils.thenIf
import kotlinx.coroutines.launch

val IVY_COLOR_PICKER_COLORS_FREE = listOf(
    //Primary
    Ivy, Purple1, Purple2, Blue, Blue2, Blue3,
    Green, Green2, Green3, Green4, Yellow,
    Orange, Orange2, Orange3, Red, Red2, Red3,
)

val IVY_COLOR_PICKER_COLORS_PREMIUM = listOf(
    //Light
    IvyLight, Purple1Light, Purple2Light, BlueLight, Blue2Light, Blue3Light,
    GreenLight, Green2Light, Green3Light, Green4Light, YellowLight,
    OrangeLight, Orange2Light, Orange3Light, RedLight, Red2Light, Red3Light,

    //Dark
    IvyDark, Purple1Dark, Purple2Dark, BlueDark, Blue2Dark, Blue3Dark,
    GreenDark, Green2Dark, Green3Dark, Green4Dark, YellowDark,
    OrangeDark, Orange2Dark, Orange3Dark, RedDark, Red2Dark, Red3Dark,
)

private data class IvyColor(
    val color: Color,
    val premium: Boolean
)

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
            if (TestingContext.inTest) return@onScreenStart //listState.scrollToItem breaks the tests
            //java.lang.IllegalStateException: pending composition has not been applied

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
                    if (it.premium) {
                        ivyContext.protectWithPaywall(
                            paywallReason = PaywallReason.PREMIUM_COLOR,
                            navigation = navigation
                        ) {
                            onColorSelected(it.color)
                        }
                    } else {
                        onColorSelected(it.color)
                    }
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

@Preview
@Composable
private fun PreviewIvyColorPicker() {
    IvyWalletComponentPreview {
        Column {
            IvyColorPicker(selectedColor = UI.colors.primary) {

            }
        }
    }
}