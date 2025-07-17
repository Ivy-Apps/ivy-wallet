package com.ivy.loans.loan

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.IvyWalletPreview
import com.ivy.legacy.ivyWalletCtx
import com.ivy.legacy.utils.navigationBarInset
import com.ivy.legacy.utils.toDensityPx
import com.ivy.ui.R
import com.ivy.wallet.ui.theme.Blue
import com.ivy.wallet.ui.theme.GradientPurple
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.Purple
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.IvyCircleButton
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.pureBlur
import kotlin.math.roundToInt

val FAB_BUTTON_SIZE = 56.dp
const val ZINDEX = 200f

@Composable
internal fun BoxWithConstraintsScope.LoanBottomBar(
    tab: LoanTab,
    selectTab: (LoanTab) -> Unit,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .background(pureBlur())
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Tab(
            icon = R.drawable.ic_custom_loan_s,
            name = "Pending",
            selected = tab == LoanTab.PENDING,
            selectedColor = Purple
        ) {
            selectTab(LoanTab.PENDING)
        }

        Spacer(Modifier.width(FAB_BUTTON_SIZE))

        Tab(
            icon = R.drawable.ic_custom_loan_s,
            name = "Completed",
            selected = tab == LoanTab.COMPLETED,
            selectedColor = Green
        ) {
            selectTab(LoanTab.COMPLETED)
        }
    }

    val ivyContext = ivyWalletCtx()
    val fabStartX = ivyContext.screenWidth / 2 - FAB_BUTTON_SIZE.toDensityPx() / 2
    val fabStartY = ivyContext.screenHeight - navigationBarInset() -
            30.dp.toDensityPx() - FAB_BUTTON_SIZE.toDensityPx()

    IvyCircleButton(
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.place(
                        x = fabStartX.roundToInt(),
                        y = fabStartY.roundToInt()
                    )
                }
            }
            .size(FAB_BUTTON_SIZE)
            .zIndex(ZINDEX),
        backgroundPadding = 8.dp,
        icon = R.drawable.ic_add,
        backgroundGradient = GradientPurple,
        hasShadow = true,
        tint = White
    ) {
        onAdd()
    }
}

@Composable
private fun RowScope.Tab(
    @DrawableRes icon: Int,
    name: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .weight(1f)
            .clip(UI.shapes.rFull)
            .clickable(onClick = onClick)
            .padding(top = 12.dp, bottom = 16.dp)
            .testTag(name.lowercase()),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IvyIcon(
            icon = icon,
            tint = if (selected) selectedColor else UI.colors.pureInverse
        )

        if (selected) {
            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = name,
                style = UI.typo.c.style(
                    fontWeight = FontWeight.Bold,
                    color = selectedColor
                )
            )
        }
    }
}

@Preview
@Composable
private fun PreviewTabularBottomBar() {
    IvyWalletPreview {
        Column(
            Modifier
                .fillMaxSize()
                .background(Blue)
        ) {
        }

        LoanBottomBar(
            tab = LoanTab.PENDING,
            selectTab = {},
            onAdd = {}
        )
    }
}
