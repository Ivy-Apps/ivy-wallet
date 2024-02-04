package com.ivy.legacy.utils

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Immutable
data class SwipeListenerState(
    val swipeOffset: Float,
    val gestureConsumed: Boolean,
)

@Composable
fun rememberSwipeListenerState(): MutableState<SwipeListenerState> = remember {
    mutableStateOf(SwipeListenerState(swipeOffset = 0f, gestureConsumed = false))
}

/**
 * sensitivity - the lower the number, the higher the sensitivity
 */
fun Modifier.verticalSwipeListener(
    sensitivity: Int,
    state: MutableState<SwipeListenerState>,
    onSwipeUp: () -> Unit = {},
    onSwipeDown: () -> Unit = {}
): Modifier {
    return this.pointerInput(Unit) {
        detectVerticalDragGestures(
            onDragEnd = {
                state.value = SwipeListenerState(
                    swipeOffset = 0f,
                    gestureConsumed = false,
                )
            },
            onVerticalDrag = { _, dragAmount ->
                // dragAmount: positive when scrolling down; negative when scrolling up
                val swipeOffset = state.value.swipeOffset + dragAmount
                var gestureConsumed = state.value.gestureConsumed

                when {
                    swipeOffset > sensitivity -> {
                        // offset > 0 when swipe down
                        if (!gestureConsumed) {
                            onSwipeDown()
                            gestureConsumed = true
                        }
                    }

                    swipeOffset < -sensitivity -> {
                        // offset < 0 when swipe up
                        if (!gestureConsumed) {
                            onSwipeUp()
                            gestureConsumed = true
                        }
                    }
                }
                state.value = SwipeListenerState(swipeOffset, gestureConsumed)
            }
        )
    }
}

/**
 * sensitivity - the lower the number, the higher the sensitivity
 */
fun Modifier.horizontalSwipeListener(
    sensitivity: Int,
    state: MutableState<SwipeListenerState>,
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {}
): Modifier {
    return this.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragEnd = {
                state.value = SwipeListenerState(
                    swipeOffset = 0f,
                    gestureConsumed = false,
                )
            },
            onHorizontalDrag = { _, dragAmount ->
                // dragAmount: positive when scrolling down; negative when scrolling up
                val swipeOffset = state.value.swipeOffset + dragAmount
                var gestureConsumed = state.value.gestureConsumed

                when {
                    swipeOffset > sensitivity -> {
                        // offset > 0 when swipe right
                        if (!gestureConsumed) {
                            onSwipeRight()
                            gestureConsumed = true
                        }
                    }

                    swipeOffset < -sensitivity -> {
                        // offset < 0 when swipe left
                        if (!gestureConsumed) {
                            onSwipeLeft()
                            gestureConsumed = true
                        }
                    }
                }
                state.value = SwipeListenerState(swipeOffset, gestureConsumed)
            }
        )
    }
}
