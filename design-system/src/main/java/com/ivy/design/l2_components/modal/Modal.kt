package com.ivy.design.l2_components.modal

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.ivy.design.l2_components.button.Btn
import com.ivy.design.l2_components.button.Icon
import com.ivy.design.l2_components.button.Text
import com.ivy.design.l2_components.modal.scope.ModalActionsScope
import com.ivy.design.l2_components.modal.scope.ModalActionsScopeImpl
import com.ivy.design.l2_components.modal.scope.ModalScope
import com.ivy.design.l2_components.modal.scope.ModalScopeImpl
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.consumeClicks
import com.ivy.design.util.isKeyboardOpen
import com.ivy.design.util.padding
import com.ivy.resources.R

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

    Actions: @Composable ModalActionsScope.() -> Unit,
    keyboardShiftsContent: Boolean = true,
    Content: @Composable ModalScope.() -> Unit
) {
    val visible by modal.visibilityState

    AnimatedVisibility(
        modifier = Modifier.fillMaxSize(),
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(mediumBlur())
                .testTag("modal_outside_blur")
                .clickable(
                    onClick = {
                        modal.hide()
                    },
                    enabled = visible
                )
                .zIndex(10f)
        )
    }

    AnimatedVisibility(
        modifier = Modifier.align(Alignment.BottomCenter),
        visible = visible,
        enter = slideInVertically(),
        exit = slideOutVertically()
    ) {
        val systemBottomPadding = systemPaddingBottom()
        val paddingBottomAnimated = if (keyboardShiftsContent) {
            val keyboardShown = keyboardShown()
            val keyboardShownInset = keyboardInset()

            animateDpAsState(
                targetValue = if (keyboardShown) keyboardShownInset else systemBottomPadding,
                animationSpec = tween(durationMillis = 200)
            ).value
        } else systemBottomPadding

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 24.dp) // 24 dp from the status bar (top)
                .background(UI.colors.pure, UI.shapes.r2Top)
                .clip(UI.shapes.r2Top)
                .consumeClicks() // don't close the modal when clicking on the empty space inside
                .padding(paddingBottomAnimated)
                .zIndex(11f)
        ) {
            BackHandler(enabled = modal.visibilityState.value) {
                modal.hide()
            }

            with(ModalScopeImpl(this)) {
                Content()
            }

            ModalActionsRow(
                paddingBottom = paddingBottomAnimated,
                Actions = Actions,
                onClose = { modal.hide() },
            )
        }
    }
}

@Composable
private fun ModalActionsRow(
    paddingBottom: Dp,
    Actions: @Composable ModalActionsScope.() -> Unit,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    RowWithLine(
        modifier = modifier
            .padding(bottom = paddingBottom)
            .padding(bottom = 12.dp)
    ) {
        SpacerHor(width = 24.dp)
        CloseButton(
            modifier = Modifier.testTag("modal_close_button"),
            onClick = onClose
        )
        with(ModalActionsScopeImpl(this)) {
            Actions()
        }
        SpacerHor(width = 24.dp)
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
    Btn.Icon(
        modifier = modifier,
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
                Btn.Text(text = "Okay") {

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
                Btn.Text(text = "Got it") {

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