package com.ivy.core.ui.modal

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ivy.core.domain.FlowViewModel
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.mediumBlur
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.modal.CloseButton
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.scope.ModalActionsScope
import com.ivy.design.l2_components.modal.scope.ModalActionsScopeImpl
import com.ivy.design.l2_components.modal.scope.ModalScope
import com.ivy.design.l2_components.modal.scope.ModalScopeImpl
import com.ivy.design.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <State, UiState, Event> BoxScope.ViewModelModal(
    modal: IvyModal,
    // TODO: Fix potential recomposition problems
    provideViewModel: @Composable () -> FlowViewModel<State, UiState, Event>,
    previewState: @Composable () -> UiState,
    actions: @Composable ModalActionsScope.(
        state: UiState,
        onEvent: (Event) -> Unit,
    ) -> Unit,
    keyboardShiftsContent: Boolean = true,
    level: Int = 1,
    content: @Composable ModalScope.(
        state: UiState,
        onEvent: (Event) -> Unit,
    ) -> Unit
) {
    val visible by modal.visibilityState

    AnimatedVisibility(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1_000f * level),
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(mediumBlur())
                .testTag("modal_outside_blur")
                .clickable(
                    onClick = {
                        keyboardController?.hide()
                        modal.hide()
                    },
                    enabled = visible
                )
        )
    }

    AnimatedVisibility(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .zIndex(1_100f * level),
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { fullHeight: Int -> fullHeight }
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight: Int -> fullHeight }
        )
    ) {
        val systemBottomPadding = systemPaddingBottom()
        val keyboardShown by keyboardShownState()
        val keyboardShownInset = keyboardPadding()
        val paddingBottom = if (keyboardShiftsContent) {
            animateDpAsState(
                targetValue = if (keyboardShown)
                    keyboardShownInset else systemBottomPadding,
            ).value
        } else systemBottomPadding

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 24.dp) // 24 dp from the status bar (top)
                .background(UI.colors.pure, UI.shapes.roundedTop)
                .clip(UI.shapes.roundedTop)
                .consumeClicks() // don't close the modal when clicking on the empty space inside
                .padding(bottom = paddingBottom)
        ) {
            val viewModel = if (isInPreview()) null else provideViewModel()
            val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

            BackHandler(enabled = modal.visibilityState.value) {
                modal.hide()
            }

            val modalScope = remember { ModalScopeImpl(this) }
            with(modalScope) {
                content(state) { viewModel?.onEvent(it) }
            }

            val keyboardController = LocalSoftwareKeyboardController.current
            ModalActionsRow(
                Actions = {
                    actions(state, onEvent = { viewModel?.onEvent(it) })
                },
                onClose = {
                    keyboardController?.hide()
                    modal.hide()
                },
            )
            SpacerVer(height = 12.dp)
        }
    }
}

@Composable
private fun ModalActionsRow(
    Actions: @Composable ModalActionsScope.() -> Unit,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
) {
    RowWithLine(
        // don't add horizontal padding because it'll break the line
        modifier = modifier.padding(top = 4.dp),
    ) {
        SpacerHor(width = 16.dp)
        CloseButton(
            modifier = Modifier.testTag("modal_close_button"),
            onClick = onClose
        )
        SpacerWeight(weight = 1f)
        val actionsScope = remember { ModalActionsScopeImpl(this) }
        with(actionsScope) {
            Actions()
        }
        SpacerHor(width = 16.dp)
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