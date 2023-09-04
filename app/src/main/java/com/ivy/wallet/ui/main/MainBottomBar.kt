package com.ivy.wallet.ui.main

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R
import com.ivy.wallet.ui.ivyWalletCtx
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.GradientGreen
import com.ivy.wallet.ui.theme.GradientIvy
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.IvyCircleButton
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.IvyOutlinedButton
import com.ivy.wallet.ui.theme.gradientExpenses
import com.ivy.wallet.ui.theme.modal.AddModalBackHandling
import com.ivy.wallet.ui.theme.pureBlur
import com.ivy.wallet.utils.clickableNoIndication
import com.ivy.wallet.utils.densityScope
import com.ivy.wallet.utils.lerp
import com.ivy.wallet.utils.navigationBarInset
import com.ivy.wallet.utils.springBounceFast
import com.ivy.wallet.utils.thenIf
import com.ivy.wallet.utils.toDensityDp
import com.ivy.wallet.utils.toDensityPx
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt

val FAB_BUTTON_SIZE = 56.dp
val TRN_BUTTON_CLICK_AREA_HEIGHT = 150.dp

@Composable
fun BoxWithConstraintsScope.BottomBar(
    tab: MainTab,
    selectTab: (MainTab) -> Unit,

    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onAddTransfer: () -> Unit,
    onAddPlannedPayment: () -> Unit,

    showAddAccountModal: () -> Unit,
) {
    val ivyContext = ivyWalletCtx()

    var expanded by remember { mutableStateOf(false) }

    val modalId = remember { UUID.randomUUID() }

    AddModalBackHandling(
        modalId = modalId,
        visible = expanded
    ) {
        expanded = false
    }

    val screenHeightDp = densityScope { ivyContext.screenHeight.toDp() }
    val expandedBackgroundOffset by animateDpAsState(
        targetValue = if (expanded) 0.dp else screenHeightDp,
        animationSpec = springBounceFast()
    )

    val fabRotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = springBounceFast()
    )

    val buttonsShownPercent by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = springBounceFast()
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .background(pureBlur())
            .alpha(1f - buttonsShownPercent)
            .navigationBarsPadding()
            .clickableNoIndication {
                // consume click
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Tab(
            icon = R.drawable.ic_home,
            name = stringResource(R.string.home),
            selected = tab == MainTab.HOME,
            selectedColor = Ivy
        ) {
            selectTab(MainTab.HOME)
        }

        Spacer(Modifier.width(FAB_BUTTON_SIZE))

        Tab(
            icon = R.drawable.ic_accounts,
            name = stringResource(R.string.accounts),
            selected = tab == MainTab.ACCOUNTS,
            selectedColor = Green
        ) {
            selectTab(MainTab.ACCOUNTS)
        }
    }

    if (expandedBackgroundOffset < screenHeightDp) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = expandedBackgroundOffset)
                .background(UI.colors.pure.copy(alpha = 0.95f))
                .clickableNoIndication {
                    // consume click, do nothing
                }
                .zIndex(100f)
        )
    }

    // ------------------------------------ BUTTONS--------------------------------------------------
    val fabStartX = ivyContext.screenWidth / 2 - FAB_BUTTON_SIZE.toDensityPx() / 2
    val fabStartY = ivyContext.screenHeight - navigationBarInset() -
        30.dp.toDensityPx() - FAB_BUTTON_SIZE.toDensityPx()

    TransactionButtons(
        buttonsShownPercent = buttonsShownPercent,

        fabStartX = fabStartX,
        fabStartY = fabStartY,

        onAddIncome = onAddIncome,
        onAddExpense = onAddExpense,
        onAddTransfer = onAddTransfer,
        onAddPlannedPayment = onAddPlannedPayment
    )

    var dragOffset by remember {
        mutableStateOf(Offset.Zero)
    }
    // + & x button
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
            .rotate(fabRotation)
            .zIndex(200f)
            .thenIf(tab == MainTab.HOME) {
                pointerInput(Unit) {
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
                                    onAddExpense()
                                }

                                dragOffset.x < -horizontalThreshold &&
                                    dragOffset.y < -verticalThreshold -> {
                                    // swipe up left
                                    dragOffset = Offset.Zero // prevent double open of the screen
                                    onAddIncome()
                                }

                                dragOffset.x > horizontalThreshold &&
                                    dragOffset.y < -verticalThreshold -> {
                                    // swipe up right
                                    dragOffset = Offset.Zero // prevent double open of the screen
                                    onAddTransfer()
                                }
                            }
                        }
                    )
                }
            }
            .testTag("fab_add"),
        backgroundPadding = 8.dp,
        icon = R.drawable.ic_add,
        backgroundGradient = when (tab) {
            MainTab.HOME -> {
                if (!expanded) GradientIvy else Gradient.solid(UI.colors.gray)
            }
            MainTab.ACCOUNTS -> {
                GradientGreen
            }
        },
        hasShadow = !expanded,
        tint = when (tab) {
            MainTab.HOME -> White
            MainTab.ACCOUNTS -> White
        }
    ) {
        when (tab) {
            MainTab.HOME -> {
                expanded = !expanded
            }
            MainTab.ACCOUNTS -> {
                showAddAccountModal()
            }
        }
    }
}

@Composable
private fun TransactionButtons(
    buttonsShownPercent: Float,

    fabStartX: Float,
    fabStartY: Float,

    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onAddTransfer: () -> Unit,
    onAddPlannedPayment: () -> Unit,
) {
    val ivyContext = ivyWalletCtx()

    val bH = 48.dp
    val bV = 20.dp // 24.dp
    val bCenterV = 74.dp // 80.dp

    if (buttonsShownPercent > 0.01f) {
        val buttonLeftX = bH.toDensityPx()
        val buttonRightX = ivyContext.screenWidth - bH.toDensityPx() - FAB_BUTTON_SIZE.toDensityPx()

        val sideButtonsY = fabStartY - bV.toDensityPx() - FAB_BUTTON_SIZE.toDensityPx()
        val buttonCenterY = fabStartY - bCenterV.toDensityPx() - FAB_BUTTON_SIZE.toDensityPx()

        val clickAreaWidth = ivyContext.screenWidth / 3

        IvyOutlinedButton(
            modifier = Modifier
                .layout { measurable, constraints ->
                    val placealbe = measurable.measure(constraints)
                    layout(placealbe.width, placealbe.height) {
                        placealbe.place(
                            x = ivyContext.screenWidth / 2 - placealbe.width / 2,
                            y = buttonCenterY.roundToInt() - 48.dp.roundToPx() - placealbe.height - FAB_BUTTON_SIZE.roundToPx()
                        )
                    }
                }
                .alpha(buttonsShownPercent)
                .zIndex(200f),
            iconStart = R.drawable.ic_planned_payments,
            text = stringResource(R.string.add_planned_payment),
            solidBackground = true
        ) {
            onAddPlannedPayment()
        }

        // Add INCOME ------------------------------------------------------------------------------
        AddIncomeButton(
            buttonsShownPercent = buttonsShownPercent,
            fabStartX = fabStartX,
            fabStartY = fabStartY,
            buttonLeftX = buttonLeftX,
            sideButtonsY = sideButtonsY,
            clickAreaWidth = clickAreaWidth,
            onAddIncome = onAddIncome
        )
        // Add INCOME ------------------------------------------------------------------------------

        // Add EXPENSE ------------------------------------------------------------------------------
        AddExpenseButton(
            buttonsShownPercent = buttonsShownPercent,
            fabStartX = fabStartX,
            fabStartY = fabStartY,
            buttonCenterY = buttonCenterY,
            clickAreaWidth = clickAreaWidth,
            onAddExpense = onAddExpense
        )
        // Add EXPENSE ------------------------------------------------------------------------------

        // Add TRANSFER ----------------------------------------------------------------------------
        AddTransferButton(
            buttonsShownPercent = buttonsShownPercent,
            fabStartX = fabStartX,
            fabStartY = fabStartY,
            buttonRightX = buttonRightX,
            sideButtonsY = sideButtonsY,
            clickAreaWidth = clickAreaWidth,
            onAddTransfer = onAddTransfer
        )
        // Add TRANSFER ----------------------------------------------------------------------------
    }
}

@Composable
private fun AddIncomeButton(
    buttonsShownPercent: Float,
    fabStartX: Float,
    fabStartY: Float,
    buttonLeftX: Float,
    sideButtonsY: Float,
    clickAreaWidth: Int,
    onAddIncome: () -> Unit
) {
    IvyCircleButton(
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)

                val x = lerp(fabStartX, buttonLeftX, buttonsShownPercent)
                val y = lerp(
                    fabStartY,
                    sideButtonsY - FAB_BUTTON_SIZE.roundToPx(),
                    buttonsShownPercent
                )

                layout(placeable.width, placeable.height) {
                    placeable.place(
                        x = x.roundToInt(),
                        y = y.roundToInt()
                    )
                }
            }
            .size(FAB_BUTTON_SIZE)
            .zIndex(200f),
        icon = R.drawable.ic_income,
        backgroundGradient = GradientGreen,
        tint = White,
        onClick = onAddIncome
    )

    Text(
        modifier = Modifier
            .width(FAB_BUTTON_SIZE + 16.dp)
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.place(
                        x = buttonLeftX.roundToInt() - 8.dp.roundToPx(),
                        y = (sideButtonsY + 12.dp.toPx()).roundToInt()
                    )
                }
            }
            .alpha(buttonsShownPercent)
            .clickableNoIndication {
                onAddIncome()
            }
            .zIndex(200f),
        text = stringResource(R.string.add_income_uppercase),
        style = UI.typo.c.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    )

    // Click area
    Spacer(
        modifier = Modifier
            .size(
                width = clickAreaWidth.toDensityDp(),
                height = TRN_BUTTON_CLICK_AREA_HEIGHT
            )
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)

                layout(placeable.width, placeable.height) {
                    placeable.place(
                        x = 0,
                        y = sideButtonsY.roundToInt() - FAB_BUTTON_SIZE.roundToPx() - 16.dp.roundToPx()
                    )
                }
            }
            .zIndex(199f)
            .clickableNoIndication {
                onAddIncome()
            }
    )
}

@Composable
private fun AddExpenseButton(
    buttonsShownPercent: Float,
    fabStartX: Float,
    fabStartY: Float,
    buttonCenterY: Float,
    clickAreaWidth: Int,
    onAddExpense: () -> Unit
) {
    IvyCircleButton(
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)

                val y =
                    lerp(
                        fabStartY,
                        buttonCenterY - FAB_BUTTON_SIZE.roundToPx(),
                        buttonsShownPercent
                    )

                layout(placeable.width, placeable.height) {
                    placeable.place(
                        x = fabStartX.roundToInt(),
                        y = y.roundToInt()
                    )
                }
            }
            .size(FAB_BUTTON_SIZE)
            .zIndex(200f),
        icon = R.drawable.ic_expense,
        backgroundGradient = gradientExpenses(),
        horizontalGradient = false,
        tint = White,
        onClick = onAddExpense
    )

    Text(
        modifier = Modifier
            .width(FAB_BUTTON_SIZE + 16.dp)
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.place(
                        x = fabStartX.roundToInt() - 8.dp.roundToPx(),
                        y = (buttonCenterY + 12.dp.toPx()).roundToInt()
                    )
                }
            }
            .alpha(buttonsShownPercent)
            .clickableNoIndication {
                onAddExpense()
            }
            .zIndex(200f),
        text = stringResource(R.string.add_expense_uppercase),
        style = UI.typo.c.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    )

    // Click area
    Spacer(
        modifier = Modifier
            .size(
                width = clickAreaWidth.toDensityDp(),
                height = TRN_BUTTON_CLICK_AREA_HEIGHT
            )
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)

                layout(placeable.width, placeable.height) {
                    placeable.place(
                        x = clickAreaWidth,
                        y = buttonCenterY.roundToInt() - FAB_BUTTON_SIZE.roundToPx() - 16.dp.roundToPx()
                    )
                }
            }
            .zIndex(199f)
            .clickableNoIndication {
                onAddExpense()
            }
    )
}

@Composable
private fun AddTransferButton(
    buttonsShownPercent: Float,
    fabStartX: Float,
    fabStartY: Float,
    buttonRightX: Float,
    sideButtonsY: Float,
    clickAreaWidth: Int,
    onAddTransfer: () -> Unit
) {
    IvyCircleButton(
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)

                val x = lerp(fabStartX, buttonRightX, buttonsShownPercent)
                val y = lerp(
                    fabStartY,
                    sideButtonsY - FAB_BUTTON_SIZE.roundToPx(),
                    buttonsShownPercent
                )

                layout(placeable.width, placeable.height) {
                    placeable.place(
                        x = x.roundToInt(),
                        y = y.roundToInt()
                    )
                }
            }
            .size(FAB_BUTTON_SIZE)
            .zIndex(200f),
        icon = R.drawable.ic_transfer,
        backgroundGradient = GradientIvy,
        tint = White,
        onClick = onAddTransfer
    )

    Text(
        modifier = Modifier
            .width(FAB_BUTTON_SIZE + 16.dp)
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.place(
                        x = buttonRightX.roundToInt() - 8.dp.roundToPx(),
                        y = (sideButtonsY + 12.dp.toPx()).roundToInt()
                    )
                }
            }
            .alpha(buttonsShownPercent)
            .clickableNoIndication {
                onAddTransfer()
            }
            .zIndex(200f),
        text = stringResource(R.string.account_transfer),
        style = UI.typo.c.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    )

    // Click area
    Spacer(
        modifier = Modifier
            .size(
                width = clickAreaWidth.toDensityDp(),
                height = TRN_BUTTON_CLICK_AREA_HEIGHT
            )
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)

                layout(placeable.width, placeable.height) {
                    placeable.place(
                        x = 2 * clickAreaWidth,
                        y = sideButtonsY.roundToInt() - FAB_BUTTON_SIZE.roundToPx() - 16.dp.roundToPx()
                    )
                }
            }
            .zIndex(199f)
            .clickableNoIndication {
                onAddTransfer()
            }
    )
}

@Composable
private fun RowScope.Tab(
    @DrawableRes icon: Int,
    name: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
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
