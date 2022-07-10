package com.ivy.wallet.ui.theme.modal

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ivy.base.IvyWalletPreview
import com.ivy.base.ivyWalletCtx
import com.ivy.design.l0_system.UI
import com.ivy.frp.view.navigation.Navigation
import com.ivy.frp.view.navigation.navigation
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.wallet.ui.theme.components.ActionsRow
import com.ivy.wallet.ui.theme.components.CloseButton
import com.ivy.wallet.ui.theme.gradientCutBackgroundTop
import com.ivy.wallet.ui.theme.mediumBlur
import com.ivy.wallet.utils.*
import java.util.*
import kotlin.math.roundToInt


private const val DURATION_BACKGROUND_BLUR_ANIM = 400
const val DURATION_MODAL_ANIM = 200

@Composable
fun BoxScope.IvyModal(
    id: UUID?,
    visible: Boolean,
    dismiss: () -> Unit,
    SecondaryActions: (@Composable () -> Unit)? = null,
    PrimaryAction: @Composable () -> Unit,
    scrollState: ScrollState? = rememberScrollState(),
    shiftIfKeyboardShown: Boolean = true,
    includeActionsRowPadding: Boolean = true,
    Content: @Composable ColumnScope.() -> Unit
) {
    val rootView = LocalView.current
    var keyboardShown by remember { mutableStateOf(false) }

    onScreenStart {
        rootView.addKeyboardListener {
            keyboardShown = it
        }
    }

    val keyboardShownInsetDp by animateDpAsState(
        targetValue = densityScope {
            if (keyboardShown) keyboardOnlyWindowInsets().bottom.toDp() else 0.dp
        },
        animationSpec = tween(DURATION_MODAL_ANIM)
    )
    val navBarPadding by animateDpAsState(
        targetValue = densityScope {
            if (keyboardShown) 0.dp else navigationBarInsets().bottom.toDp()
        },
        animationSpec = tween(DURATION_MODAL_ANIM)
    )
    val blurAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(DURATION_BACKGROUND_BLUR_ANIM),
        visibilityThreshold = 0.01f
    )
    val modalPercentVisible by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(DURATION_MODAL_ANIM),
        visibilityThreshold = 0.01f
    )

    if (visible || blurAlpha > 0.01f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(blurAlpha)
                .background(mediumBlur())
                .testTag("modal_outside_blur")
                .clickable(
                    onClick = {
                        hideKeyboard(rootView)
                        dismiss()
                    },
                    enabled = visible
                )
                .zIndex(1000f)
        )
    }

    if (visible || modalPercentVisible > 0.01f) {
        var actionsRowHeight by remember { mutableStateOf(0) }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)

                    val height = placeable.height
                    val y = height * (1 - modalPercentVisible)

                    layout(placeable.width, height) {
                        placeable.placeRelative(
                            0,
                            y.roundToInt()
                        )
                    }
                }
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 24.dp)
                .background(UI.colors.pure, UI.shapes.r2Top)
                .consumeClicks()
                .thenIf(scrollState != null) {
                    verticalScroll(scrollState!!)
                }
                .zIndex(1000f)
        ) {
            ModalBackHandling(
                modalId = id,
                visible = visible,
                dismiss = dismiss
            )

            Content()

            //Bottom padding
            if (includeActionsRowPadding) {
                Spacer(Modifier.height(densityScope { actionsRowHeight.toDp() }))
            }

            if (shiftIfKeyboardShown) {
                Spacer(Modifier.height(keyboardShownInsetDp))
            }
        }

        ModalActionsRow(
            visible = visible,
            modalPercentVisible = modalPercentVisible,
            keyboardShownInsetDp = keyboardShownInsetDp,
            navBarPadding = navBarPadding,
            onHeightChanged = {
                actionsRowHeight = it
            },
            onClose = {
                hideKeyboard(rootView)
                dismiss()
            },
            SecondaryActions = SecondaryActions,
            PrimaryAction = PrimaryAction
        )
    }
}

@Composable
private fun ModalBackHandling(
    modalId: UUID?,
    visible: Boolean,
    dismiss: () -> Unit
) {
    AddModalBackHandling(
        modalId = modalId,
        visible = visible,
        action = {
            dismiss()
        }
    )
}

@Composable
fun AddModalBackHandling(
    modalId: UUID?,
    visible: Boolean,
    action: () -> Unit
) {
    val nav = navigation()
    DisposableEffect(visible) {
        if (visible) {
            val lastModalBackHandlingId = nav.lastModalBackHandlerId()

            if (modalId != null && modalId != lastModalBackHandlingId) {
                nav.modalBackHandling.add(
                    Navigation.ModalBackHandler(
                        id = modalId,
                        onBackPressed = {
                            if (visible) {
                                action()
                                true
                            } else {
                                false
                            }
                        }
                    )
                )
            }
        }

        onDispose {
            val lastModalBackHandlingId = nav.lastModalBackHandlerId()
            if (modalId != null && lastModalBackHandlingId == modalId) {
                removeLastBackHandlerSafe(nav)
            }
        }
    }
}

private fun removeLastBackHandlerSafe(nav: Navigation) {
    if (nav.modalBackHandling.isNotEmpty()) {
        nav.modalBackHandling.pop()
    }
}

@Composable
fun ModalActionsRow(
    visible: Boolean,
    modalPercentVisible: Float,
    keyboardShownInsetDp: Dp,
    navBarPadding: Dp,

    onHeightChanged: (Int) -> Unit,

    onClose: () -> Unit,
    SecondaryActions: (@Composable () -> Unit)? = null,
    PrimaryAction: @Composable () -> Unit
) {
    if (visible || modalPercentVisible > 0.01f) {
        val ivyContext = ivyWalletCtx()
        ActionsRow(
            modifier = Modifier
                .onSizeChanged {
                    onHeightChanged(it.height)
                }
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)

                    val systemOffsetBottom = keyboardShownInsetDp.toPx()
                    val visibleHeight = placeable.height * modalPercentVisible
                    val y = ivyContext.screenHeight - visibleHeight - systemOffsetBottom

                    layout(placeable.width, placeable.height) {
                        placeable.place(
                            0,
                            y.roundToInt()
                        )
                    }
                }
                .gradientCutBackgroundTop(
                    endY = 16.dp
                )
                .padding(top = 8.dp, bottom = 12.dp)
                .padding(bottom = navBarPadding)
                .zIndex(1100f)
        ) {
            Spacer(Modifier.width(24.dp))

            CloseButton(
                modifier = Modifier.testTag("modal_close_button"),
                onClick = onClose
            )

            SecondaryActions?.invoke()

            Spacer(Modifier.weight(1f))

            PrimaryAction()

            Spacer(Modifier.width(24.dp))
        }
    }
}

@Preview
@Composable
private fun PreviewIvyModal_minimal() {
    IvyWalletPreview {
        IvyModal(
            id = UUID.randomUUID(),
            visible = true,
            PrimaryAction = {
                ModalSave {

                }
            },
            dismiss = {}
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "My first Ivy Modal"
            )

            ModalPreviewActionRowSpacer()
        }
    }
}

@Composable
fun ModalPreviewActionRowSpacer() {
    Spacer(Modifier.height(modalPreviewActionRowHeight()))
}

@Composable
fun modalPreviewActionRowHeight() = 80.dp