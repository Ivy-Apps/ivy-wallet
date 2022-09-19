package com.ivy.design.l2_components.modal

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.mediumBlur
import com.ivy.design.l1_buildingBlocks.IvyText
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l1_buildingBlocks.data.solidWithBorder
import com.ivy.design.l2_components.Button
import com.ivy.design.l2_components.IconButton
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.consumeClicks
import com.ivy.design.util.isKeyboardOpen
import com.ivy.design.util.padding
import com.ivy.resources.R
import kotlin.math.roundToInt

private const val DURATION_BACKGROUND_BLUR_ANIM = 400
const val DURATION_MODAL_ANIM = 200

private val MODAL_ACTIONS_PADDING_BOTTOM = 12.dp
private val MODAL_ACTIONS_HEIGHT = 48.dp
private val MODAL_ACTIONS_OFFSET = MODAL_ACTIONS_HEIGHT + MODAL_ACTIONS_PADDING_BOTTOM

@Immutable
data class IvyModal(
    val visibilityState: MutableState<Boolean> = mutableStateOf(false)
) {
    fun hide() {
        visibilityState.value = false
    }

    fun show() {
        visibilityState.value = true
    }
}

@Composable
fun BoxScope.Modal(
    modal: IvyModal,
    Actions: @Composable RowScope.() -> Unit,

    keyboardShiftsContent: Boolean = true,
    Content: @Composable ColumnScope.() -> Unit
) {
    val visible by modal.visibilityState
    val percentVisible by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(DURATION_MODAL_ANIM),
        visibilityThreshold = 0.01f
    )

    val systemBottomPadding = systemPaddingBottom()
    val paddingBottomAnimated = if (keyboardShiftsContent) {
        val keyboardShown = keyboardShown()
        val keyboardShownInset = keyboardInset()

        animateDpAsState(
            targetValue = if (keyboardShown) keyboardShownInset else systemBottomPadding,
            animationSpec = tween(DURATION_MODAL_ANIM)
        ).value
    } else systemBottomPadding

    val blurAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(DURATION_BACKGROUND_BLUR_ANIM),
        visibilityThreshold = 0.01f
    )
    if (visible || blurAlpha > 0.01f) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .alpha(blurAlpha)
                .background(mediumBlur())
                .testTag("modal_outside_blur")
                .clickable(
                    onClick = {
                        modal.hide()
                    },
                    enabled = visible
                )
                .zIndex(1000f)
        )
    }

    if (visible || percentVisible > 0.01f) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val height = placeable.height
                    val y = height * (1 - percentVisible)

                    layout(placeable.width, height) {
                        placeable.placeRelative(x = 0, y = y.roundToInt())
                    }
                }
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 24.dp) // 24 dp from the status bar (top)
                .background(UI.colors.pure, UI.shapes.r2Top)
                .clip(UI.shapes.r2Top)
                .consumeClicks() // don't close the modal when clicking on the empty space inside
                .padding(bottom = MODAL_ACTIONS_OFFSET + paddingBottomAnimated)
                .zIndex(1001f)
        ) {
            BackHandler(enabled = modal.visibilityState.value) {
                modal.hide()
            }

            Content()
        }

        ModalActionsRow(
            visible = visible,
            modalPercentVisible = percentVisible,
            paddingBottom = paddingBottomAnimated,
            Actions = Actions,
            onClose = { modal.hide() },
        )
    }
}

@Composable
private fun ModalActionsRow(
    visible: Boolean,
    modalPercentVisible: Float,
    paddingBottom: Dp,

    Actions: @Composable RowScope.() -> Unit,
    onClose: () -> Unit,
) {
    if (visible || modalPercentVisible > 0.01f) {
        // used only to get the screen height
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp

        RowWithLine(
            modifier = Modifier
                .height(MODAL_ACTIONS_HEIGHT)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)

                    val bottomOffset = paddingBottom.toPx() + MODAL_ACTIONS_PADDING_BOTTOM.toPx()
                    val visibleHeight = placeable.height * modalPercentVisible
                    val y = screenHeight.toPx() - visibleHeight - bottomOffset

                    layout(placeable.width, placeable.height) {
                        placeable.place(x = 0, y = y.roundToInt())
                    }
                }
                .zIndex(1100f)
        ) {
            SpacerHor(width = 24.dp)
            CloseButton(
                modifier = Modifier.testTag("modal_close_button"),
                onClick = onClose
            )
            Actions()
            SpacerHor(width = 24.dp)
        }
    }
}

@Composable
private fun RowWithLine(
    modifier: Modifier = Modifier,
    lineColor: Color = UI.colors.medium,
    Content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val height = this.size.height
                val width = this.size.width

                drawLine(
                    color = lineColor,
                    strokeWidth = 2.dp.toPx(),
                    start = Offset(x = 0f, y = height / 2),
                    end = Offset(x = width, y = height / 2)
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Content()
    }
}

@Composable
fun CloseButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        icon = R.drawable.ic_dismiss,
        iconTint = UI.colors.pureInverse,
        background = solidWithBorder(
            solid = UI.colors.pure,
            borderColor = UI.colors.medium,
            borderWidth = 2.dp,
            shape = CircleShape,
            padding = padding(all = 6.dp)
        ),
        onClick = onClick
    )
}

@Composable
private fun keyboardShown(): Boolean {
    var keyboardOpen by remember { mutableStateOf(false) }
    val rootView = LocalView.current

    DisposableEffect(Unit) {
        val keyboardListener = {
            // check keyboard state after this layout
            val isOpenNew = isKeyboardOpen(rootView)

            // since the observer is hit quite often, only callback when there is a change.
            if (isOpenNew != keyboardOpen) {
                keyboardOpen = isOpenNew
            }
        }

        rootView.doOnLayout {
            // get initial state of keyboard
            keyboardOpen = isKeyboardOpen(rootView)

            // whenever the layout resizes/changes, callback with the state of the keyboard.
            rootView.viewTreeObserver.addOnGlobalLayoutListener(keyboardListener)
        }

        onDispose {
            // stop keyboard updates
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(keyboardListener)
        }
    }

    return keyboardOpen
}

// region Insets
/**
 * @return system's bottom inset (nav buttons or bottom nav)
 */
@Composable
private fun systemPaddingBottom(): Dp {
    val rootView = LocalView.current
    val densityScope = LocalDensity.current
    return remember(rootView) {
        val insetPx =
            WindowInsetsCompat.toWindowInsetsCompat(rootView.rootWindowInsets, rootView)
                .getInsets(WindowInsetsCompat.Type.navigationBars())
                .bottom
        with(densityScope) { insetPx.toDp() }
    }
}

@Composable
private fun keyboardInset(): Dp {
    val rootView = LocalView.current
    val densityScope = LocalDensity.current
    return remember(rootView) {
        val insetPx =
            WindowInsetsCompat.toWindowInsetsCompat(rootView.rootWindowInsets, rootView)
                .getInsets(WindowInsetsCompat.Type.ime())
                .bottom
        with(densityScope) { insetPx.toDp() }
    }
}
// endregion

// region Previews
@Preview
@Composable
private fun Preview_FullScreen() {
    val modal = IvyModal()
    modal.show()

    IvyPreview {
        Modal(
            modal = modal,
            Actions = {
                SpacerWeight(weight = 1f)
                Button(text = "Okay") {

                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
            )
        }
    }
}

@Preview
@Composable
private fun Preview_Partial() {
    val modal = IvyModal()
    modal.show()

    IvyPreview {
        Modal(
            modal = modal,
            Actions = {
                SpacerWeight(weight = 1f)
                Button(text = "Got it") {

                }
            }
        ) {
            SpacerVer(height = 32.dp)
            IvyText(
                modifier = Modifier.padding(start = 24.dp),
                text = "Title",
                typo = UI.typo.h2
            )
            SpacerVer(height = 32.dp)
        }
    }
}
// endregion