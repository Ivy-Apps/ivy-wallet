package com.ivy.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.IconRes
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.B2
import com.ivy.design.l3_ivyComponents.button.ButtonFeeling
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.ButtonVisibility
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.consumeClicks
import com.ivy.navigation.destinations.main.Main.Tab
import kotlin.math.abs

@Composable
internal fun BottomBar(
    selectedTab: Tab,
    onActionClick: (Tab) -> Unit,
    onActionSwipeUp: () -> Unit,
    onActionSwipeDiagonalLeft: () -> Unit,
    onActionSwipeDiagonalRight: () -> Unit,
    onHomeClick: () -> Unit,
    onAccountsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = UI.colors.medium.copy(alpha = 0.9f),
                shape = UI.shapes.rounded
            )
            .consumeClicks()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Tab(
            text = stringResource(R.string.home),
            selected = selectedTab == Tab.Home,
            icon = R.drawable.ic_home,
            modifier = Modifier.weight(1f),
            onClick = onHomeClick
        )
        ActionButton(
            onSwipeUp = onActionSwipeUp,
            onSwipeDiagonalLeft = onActionSwipeDiagonalLeft,
            onSwipeDiagonalRight = onActionSwipeDiagonalRight,
        ) {
            onActionClick(selectedTab)
        }
        Tab(
            text = stringResource(R.string.accounts),
            selected = selectedTab == Tab.Accounts,
            icon = R.drawable.ic_accounts,
            modifier = Modifier.weight(1f),
            onClick = onAccountsClick,
        )
    }
}

@Composable
private fun Tab(
    text: String,
    selected: Boolean,
    @DrawableRes
    icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(UI.shapes.rounded)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconRes(
            icon = icon,
            tint = if (selected) UI.colors.primary else UI.colorsInverted.pure,
        )
        if (selected) {
            SpacerVer(height = 8.dp)
            B2(
                text = text,
                color = UI.colors.primary
            )
        }
    }
}

@Composable
private fun ActionButton(
    onSwipeUp: () -> Unit,
    onSwipeDiagonalLeft: () -> Unit,
    onSwipeDiagonalRight: () -> Unit,
    onClick: () -> Unit
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    IvyButton(
        modifier = Modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragCancel = {
                    dragOffset = Offset.Zero
                },
                onDragEnd = {
                    dragOffset = Offset.Zero
                },
                onDrag = { _, dragAmount ->
                    dragOffset += dragAmount

                    val horizontalThreshold = 40
                    val verticalThreshold = 60

                    when {
                        abs(dragOffset.x) < horizontalThreshold &&
                                dragOffset.y < -verticalThreshold -> {
                            // swipe up
                            dragOffset = Offset.Zero // prevent double open of the screen
                            onSwipeUp()
                        }
                        dragOffset.x < -horizontalThreshold &&
                                dragOffset.y < -verticalThreshold -> {
                            //swipe up left
                            dragOffset = Offset.Zero // prevent double open of the screen
                            onSwipeDiagonalLeft()
                        }
                        dragOffset.x > horizontalThreshold &&
                                dragOffset.y < -verticalThreshold -> {
                            // swipe up right
                            dragOffset = Offset.Zero // prevent double open of the screen
                            onSwipeDiagonalRight()
                        }
                    }
                }
            )
        },
        size = ButtonSize.Small,
        visibility = ButtonVisibility.Focused,
        feeling = ButtonFeeling.Positive,
        text = null,
        icon = R.drawable.ic_round_add_24,
        onClick = onClick
    )
}


// region Previews
@Preview
@Composable
private fun Preview_Home() {
    ComponentPreview {
        BottomBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            selectedTab = Tab.Home,
            onActionClick = {},
            onActionSwipeUp = {},
            onActionSwipeDiagonalLeft = {},
            onActionSwipeDiagonalRight = {},
            onHomeClick = { },
            onAccountsClick = {}
        )
    }
}
// endregion